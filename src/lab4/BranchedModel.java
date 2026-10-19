package lab4;

import lab3.*;
import lab3.Process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BranchedModel extends Model {
    private final int servicesNumber;
    private final double simulationTime;
    private final int branchingNumber;

    private Create start = null;
    private final List<Process> services = new ArrayList<>();
    private final List<Process> endings = new ArrayList<>();
    private final IDistribution startDistribution = new Distributions.Exp(2);
    private final IDistribution distribution = new Distributions.Exp(3);

    public double timeToSimulate = 0;

    public BranchedModel(int servicesNumber, double simulationTime, int branchingNumber) {
        this.servicesNumber = servicesNumber;
        this.simulationTime = simulationTime;
        this.branchingNumber = branchingNumber;

        this.build();
    }

    private void build(){
        start = new Create(this, this::arrival);
        start.setDistributionRule((el) -> startDistribution);
        IRouter startRouter = start.setRouter(new ProbabilityRouter());
        services.add(new Process(this));
        startRouter.addRoute(new Route(services.getFirst(), start, 1.0));

        Sensor completionSensor = new Sensor(e -> e == EventType.COMPLETE, this::senseCompletion);

        for(int i=0; i < servicesNumber; i++){
            Process service = services.get(i);
            service.setRouter(new ProbabilityRouter());
            service.setQueue(new ServiceQueue<>(Integer.MAX_VALUE));
            service.addSensor(completionSensor);
            service.setDistributionRule(el -> distribution);

            if(i+1 == servicesNumber) continue;

            Process p = new Process(this);
            services.add(p);
        }

        for(int i=0; i < servicesNumber; i++){
            Process p = services.get(i);
            IRouter router = p.getRouter();
            if(branchingNumber * i + 1 >= servicesNumber) break;
            for(int j=0; j < branchingNumber; j++){
                if(branchingNumber * i + j + 1 >= servicesNumber) break;
                Process to = services.get(branchingNumber * i + j + 1);
                router.addRoute(new Route(
                        to,
                        p,
                        1.0 / Math.min(
                                branchingNumber,
                                Math.abs((branchingNumber * i + 1) - servicesNumber)
                        )
                ));
            }
        }

        for(Process p : services){
            IRouter router = p.getRouter();
            if(router.getRoutes().isEmpty()) endings.add(p);
        }
    }

    public void runSimulation(){
        addEvent(0.0, () -> start.complete());
        this.simulate(simulationTime, this::updateStatistics);
    }

    public Map<String, Double> getResultStatistics(){
        double avgTimeToSimulate = (double) timeToSimulate / 1_000_000;
        double avgEventsCompleted = (double) getEventsCompleted();
        double avgEventsAdded = (double) getEventsAdded();
        double avgCreatedOrders = (double) start.getQuantity();

        double avgDisposedOrders = 0;
        for (Process p : endings) {
            avgDisposedOrders += p.getQuantity();
        }

        double avgOrderPathLen = (double) avgEventsCompleted / avgCreatedOrders;
        double avgEventIntensity = (double) getEventsAdded() / getTime();


        Map<String, Double> statistics = super.getStatisticResults();
        statistics.put("Max Depth (services) ", (double) getMaxDepth());
        statistics.put("Avg Time to Simulate (ms) ", avgTimeToSimulate);
        statistics.put("Avg Events completed", avgEventsCompleted);
        statistics.put("Avg Events added", avgEventsAdded);
        statistics.put("Avg Orders created ", avgCreatedOrders);
        statistics.put("Avg Order path len", avgOrderPathLen);
        statistics.put("Avg Orders disposed ", avgDisposedOrders);
        statistics.put("Avg Events intensity (e/tp)", avgEventIntensity);

        return statistics;
    }

    private Order arrival(){
        return new Order(getTime());
    }

    private void updateStatistics(Double delta){
        for (Process p : services) {
            p.updateStatistics(delta);
        }
    }

    private void senseCompletion(double activationTime, Order o){
        super.setEventsCompleted(super.getEventsCompleted() +1);
    }

    private int getMaxDepth() {
        if (servicesNumber == 0) {
            return 0;
        }

        if (branchingNumber <= 1) {
            return servicesNumber;
        }

        return (int) Math.ceil(
                Math.log((branchingNumber - 1.0) * servicesNumber + 1)
                        / Math.log(branchingNumber)
        );
    }
}
