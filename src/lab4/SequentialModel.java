package lab4;

import lab3.*;
import lab3.Process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SequentialModel extends Model {
    private final int servicesNumber;
    private final double simulationTime;
    private Create start = null;
    private Process ending = null;
    private final List<Process> services = new ArrayList<>();
    private final IDistribution startDistribution = new Distributions.Exp(3);
    private final IDistribution distribution = new Distributions.Exp(2);
    public double timeToSimulate = 0;


    public SequentialModel(int N, double simTime) {
        servicesNumber = N;
        simulationTime = simTime;

        this.build();
    }

    public void build(){
        start = new Create(this, this::arrival);
        start.setDistributionRule((el) -> startDistribution);
        services.add(new Process(this));
        start.setRouter(new ProbabilityRouter())
                .addRoutes(new Route(services.getFirst(), start, (double) 1.0));

        Sensor completionSensor = new Sensor(e -> e == EventType.COMPLETE, this::senseCompletion);

        start.addSensor(completionSensor);

        for(int i=0; i < servicesNumber; i++){
            if(i+1 != servicesNumber) services.add(new Process(this));
            Process p = services.get(i);

            p.setRouter(new ProbabilityRouter());
            p.setQueue(new ServiceQueue<>(Integer.MAX_VALUE));
            p.setDistributionRule((el) -> distribution);
            p.addSensor(completionSensor);
            if(i+1 != servicesNumber) p.getRouter().addRoutes(new Route(services.get(i+1), p, (double) 1.0));
            else ending = p;
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
        double avgDisposedOrders = (double) ending.getQuantity();
        double avgOrderPathLen = (double) avgEventsCompleted / avgCreatedOrders;
        double avgEventIntensity = (double) getEventsAdded() / getTime();

        Map<String, Double> statistics = super.getStatisticResults();
        statistics.put("Avg Time to Simulate (ms) ", avgTimeToSimulate);
        statistics.put("Avg Events completed", avgEventsCompleted);
        statistics.put("Avg Events added", avgEventsAdded);
        statistics.put("Avg Orders created ", avgCreatedOrders);
        statistics.put("Avg Order path len", avgOrderPathLen);
        statistics.put("Avg Orders disposed ", avgDisposedOrders);
        statistics.put("Avg Events intensity (e/tp)", avgEventIntensity);

        return statistics;
    }

    @Override
    public void showResults(){
        for(Map.Entry<String, Double> entry : getStatisticResults().entrySet()){
            System.out.printf("%s :\t\t\t %.5f \n", entry.getKey(), entry.getValue());
        }
    }

    private Order arrival(){
        return new Order(getTime());
    }

    private void updateStatistics(Double delta){
        for(Process p : services){
            p.updateStatistics(delta);
        }
    }

    private void senseCompletion(double activationTime, Order o){
        super.setEventsCompleted(super.getEventsCompleted() + 1);
    }

    @Override
    public void perRepReset() {
        this.setTime(0);

        for(Process p : services){
            p.getQueue().clear();
        }
    }
}
