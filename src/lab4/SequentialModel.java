package lab4;

import lab3.*;
import lab3.Process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SequentialModel extends Model {
    private final static int[] SERVICES_NUM = new int[]{10, 20, 50, 100, 250, 500, 1000};
    private final static int REPETITIONS_NUM = 5;
    private final int servicesNumber;
    private Create start = null;
    private final List<Process> services = new ArrayList<>();
    private final IDistribution distribution = new Distributions.Exp(10);
    private Process ending = null;
    private static double timeToSimulate = Double.MAX_VALUE;
    private static final double SIM_TIME = 10000;
    private static double orderInSystemTime = 0;

    private SequentialModel(int N){
        servicesNumber = N;
    }

    public static void main(String[] args) {
        for(int i=0; i < SERVICES_NUM.length; i++){
            SequentialModel analytic = new SequentialModel(SERVICES_NUM[i]);
            analytic.build();
            analytic.runSimulation(SIM_TIME);

            for(int j=0; j < REPETITIONS_NUM; j++){
                long start = System.currentTimeMillis();
                analytic.runSimulation(SIM_TIME);
                timeToSimulate = Math.min(System.currentTimeMillis() - start, timeToSimulate);
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

    private void runSimulation(double time){

    }

    private void getResultStatistics(){
        double avgOrderInSystemTime = orderInSystemTime / (SIM_TIME * REPETITIONS_NUM);


        Map<String, Double> statistics = super.getStatisticResults();
        statistics.put("Number of Reps: ", (double) REPETITIONS_NUM);
        statistics.put("Number of Services: ", (double) servicesNumber);
        statistics.put("Simulation Time: ", (double) SIM_TIME);
        statistics.put("Time to Simulate: ", (double) timeToSimulate);
        statistics.put("Created Orders: ", (double) start.getQuantity());
        statistics.put("Completed Orders: ", (double) ending.getQuantity());
        statistics.put("Avg time for order to complete system: ", avgOrderInSystemTime);
    }

    private Order arrival(){
        if(this.getOrdersInSystem() >= servicesNumber + 1) return null;
        return new Order(getTime());
    }

    private void senseDisposal(double activationTime, Order o){
        orderInSystemTime += activationTime - o.getArrivalTime();
    }
}
