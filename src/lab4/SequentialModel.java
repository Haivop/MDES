package lab4;

import lab3.*;
import lab3.Process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SequentialModel extends Model {
    private final static int[] SERVICES_NUM = new int[]{1000, 10000, 100000};
    private final static int REPETITIONS_NUM = 5;
    private final int servicesNumber;
    private Create start = null;
    private final List<Process> services = new ArrayList<>();
    private final IDistribution distribution = new Distributions.Exp(10);
    private Process ending = null;
    private static double timeToSimulate = 0;
    private static final double SIM_TIME = Double.MAX_VALUE;
    private static double orderInSystemTime = 0;

    private SequentialModel(int N){
        servicesNumber = N;
    }

    public static void main(String[] args) {
        for (int k : SERVICES_NUM) {
            SequentialModel analytic = new SequentialModel(k);

            for (int j = 0; j < REPETITIONS_NUM; j++) {
                analytic.reset();
                analytic.build();
                analytic.runSimulation();

                long start = System.currentTimeMillis();
                analytic.runSimulation();
                timeToSimulate += System.currentTimeMillis() - start;
            }

            analytic.getResultStatistics();
            analytic.showResults();
        }
    }

    private void build(){
        start = new Create(this, this::arrival);
        start.setDistributionRule((el) -> distribution);
        services.add(new Process(this));
        start.setRouter(new ProbabilityRouter())
                .addRoutes(new Route(services.getFirst(), start, (double) 1.0));

        Sensor disposalSensor = new Sensor(e -> e == EventType.COMPLETE, this::senseDisposal);

        for(int i=0; i < servicesNumber; i++){
            services.add(new Process(this));
            Process p = services.get(i);

            p.setRouter(new ProbabilityRouter());
            p.setQueue(new ServiceQueue<>(Integer.MAX_VALUE));
            p.setDistributionRule((el) -> distribution);
            if(i+1 != servicesNumber) p.getRouter().addRoutes(new Route(services.get(i+1), p, (double) 1.0));
            if(i+1 != servicesNumber) ending = p;
        }

        ending.addSensor(disposalSensor);
    }

    private void runSimulation(){
        for(int i=0; i < servicesNumber; i++){
            addEvent(0.0, () -> start.complete());
        }
        this.simulate(SIM_TIME, this::updateStatistics);
    }

    private void getResultStatistics(){
        double avgOrderInSystemTime = orderInSystemTime / (this.getTime() * REPETITIONS_NUM);
        double failures = 0;

        for (Process p : services) {
            failures += p.getFailure();
        }


        Map<String, Double> statistics = super.getStatisticResults();
        statistics.put("Avg Time to Simulate (ms) ", (double) timeToSimulate / REPETITIONS_NUM);
        statistics.put("Created Orders ", (double) start.getQuantity());
        statistics.put("Avg Completed Orders ", (double) ending.getQuantity() / REPETITIONS_NUM);
        statistics.put("Avg order completion time (time points) ", avgOrderInSystemTime);
        statistics.put("Avg failures ", failures / REPETITIONS_NUM);
    }

    @Override
    public void showResults(){
        System.out.printf("\n ------------------RESULTS(N=%d; R=%d)----------------%n",
                servicesNumber, REPETITIONS_NUM);
        for(Map.Entry<String, Double> entry : getStatisticResults().entrySet()){
            System.out.printf("%s :\t\t\t %.5f \n", entry.getKey(), entry.getValue());
        }
    }

    private Order arrival(){
        if(this.getOrdersInSystem() >= servicesNumber + 1) return null;
        setOrdersInSystem(getOrdersInSystem() +1);
        return new Order(getTime());
    }

    private void updateStatistics(Double delta){
    }

    private void senseDisposal(double activationTime, Order o){
        orderInSystemTime += activationTime - o.getArrivalTime();
    }
}
