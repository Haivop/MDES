package lab4;

import lab3.*;
import lab3.Process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BranchedModel extends Model {
    private final static int[] SERVICES_NUM = new int[]{500, 1000, 2000, 4000, 8000};
    private final static int REPETITIONS_NUM = 5;
    private static double timeToSimulate = 0;
    private static final double SIM_TIME = Double.MAX_VALUE;
    private static double orderInSystemTime = 0;

    private final int servicesNumber;
    private final List<List<Process>> branches = new ArrayList<>();
    private final int branchesNumber = 2;

    private Create start = null;
    private final IDistribution distribution = new Distributions.Exp(10);
    private final List<Process> ending = new ArrayList<>();

    private BranchedModel(int N){
        servicesNumber = N;
    }

    public static void main(String[] args) {
        for (int k : SERVICES_NUM) {
            BranchedModel analytic = new BranchedModel(k);

            analytic.build();
            analytic.runSimulation();

            for (int j = 0; j < REPETITIONS_NUM; j++) {
                analytic.reset();

                long start = System.currentTimeMillis();
                analytic.runSimulation();
                timeToSimulate += System.currentTimeMillis() - start;
            }

            analytic.getResultStatistics();
            analytic.showResults();
        }
    }

    private void build(){
        for(int i = 0; i < branchesNumber; i++) {
            branches.add(new ArrayList<>());
        }

        start = new Create(this, this::arrival);
        start.setDistributionRule((el) -> distribution);
        IRouter startRouter = start.setRouter(new ProbabilityRouter());

        for(List<Process> branch : branches){
            branch.add(new Process(this));
            startRouter.addRoute(new Route(
                branch.getFirst(),
                start,
                1.0 / branches.size()
            ));
        }

        Sensor completeSensor = new Sensor(e -> e == EventType.COMPLETE, this::senseDisposal);
        Sensor disposalSensor = new Sensor(e -> e == EventType.COMPLETE, this::senseDisposal);
        int length = servicesNumber / branches.size();

        for(int i=0; i < length; i++){
            for(List<Process> branch : branches){
                branch.add(new Process(this));
                Process p = branch.get(i);
                p.setRouter(new ProbabilityRouter());
                p.setQueue(new ServiceQueue<>(Integer.MAX_VALUE));
                p.setDistributionRule((el) -> distribution);
            }
        }

        for(int i=0; i < length; i++){
            for(List<Process> branch : branches){
                Process p = branch.get(i);
                if(i+1 != length) {
                    IRouter r = p.getRouter();

                    for(List<Process> b : branches){
                        r.addRoute(new Route(
                                b.get(i + 1),
                                p,
                                (double) 1.0 / branches.size()
                        ));
                    }
                } else {
                    ending.add(p);
                }
            }
        }

        for(Process e : ending){
            e.addSensor(disposalSensor);
        }
    }

    private void runSimulation(){
        for(int i=0; i < servicesNumber; i++){
            addEvent(0.0, () -> start.complete());
        }
        this.simulate(SIM_TIME, this::updateStatistics);
    }

    private void getResultStatistics(){
        double avgOrderInSystemTime = orderInSystemTime / (this.getGeneralTime());
        double completedOrders = 0;

        for(Process e : ending){
            completedOrders += e.getQuantity();
        }


        Map<String, Double> statistics = super.getStatisticResults();
        statistics.put("Avg Time to Simulate (ms) ", (double) timeToSimulate / REPETITIONS_NUM);
        statistics.put("Avg Time of Simulation (time points) ", (double) this.getGeneralTime() / REPETITIONS_NUM);
        statistics.put("Created Orders ", (double) start.getQuantity() / (REPETITIONS_NUM + 1));
        statistics.put("Avg Completed Orders ", (double) completedOrders / (REPETITIONS_NUM + 1));
        statistics.put("Avg order completion time (time points) ", avgOrderInSystemTime);
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
