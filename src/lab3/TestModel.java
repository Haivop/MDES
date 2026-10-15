package lab3;

import java.util.TreeMap;

public class TestModel extends Model {
    private final IDistribution arrivalInterval = new Distributions.Exp(2);
    private final IDistribution test1Time = new Distributions.Exp(0.6);
    private final IDistribution test2Time = new Distributions.Exp(0.3);
    private final IDistribution test3Time = new Distributions.Exp(0.4);
    private final IDistribution test4Time = new Distributions.Exp(0.1);

    private Create start;
    private Process test1;
    private Process test2;
    private Process test3;
    private Process test4;

    private final double SIM_TIME = 1000000;

    public static void main(String[] args) {
        TestModel test = new TestModel();
        Create start = test.build();
        test.runSimulation(start);
        test.getResultStatistics();
        test.showResults();
    }

    private Create build() {
        start = new Create(this, this::arrival);
        test1 = new Process(this);
        test2 = new Process(this);
        test3 = new Process(this);
        test4 = new Process(this);

        start.setName("НАДХОДЖЕННЯ");
        start.setRouter(new ProbabilityRouter())
            .addRoutes(
                new Route(test1, start, (double) 1)
            );
        start.setDistributionRule((el) -> arrivalInterval);

        test1.setName("СМО 1");
        test1.setRouter(new ProbabilityRouter())
                .addRoutes(
                        new Route(test2, test1, (double) 0.15),
                        new Route(test3, test1, (double) 0.13),
                        new Route(test4, test1, (double) 0.3)
                );
        test1.setDistributionRule((el) -> test1Time);
        test1.setQueue(new ServiceQueue<>(Integer.MAX_VALUE));


        test2.setName("СМО 2");
        test2.setRouter(new ProbabilityRouter())
            .addRoutes(
                    new Route(test1, test2, (double) 1)
            );
        test2.setDistributionRule((el) -> test2Time);
        test2.setQueue(new ServiceQueue<>(Integer.MAX_VALUE));


        test3.setName("СМО 3");
        test3.setRouter(new ProbabilityRouter())
            .addRoutes(
                    new Route(test1, test3, (double) 1)
            );
        test3.setDistributionRule((el) -> test3Time);
        test3.setQueue(new ServiceQueue<>(Integer.MAX_VALUE));


        test4.setName("СМО 4");
        test4.setRouter(new ProbabilityRouter())
            .addRoutes(
                new Route(test1, test4, (double) 1)
            );
        test4.setDistributionRule((el) -> test4Time);
        test4.setDeviceCount(2);
        test4.setQueue(new ServiceQueue<>(Integer.MAX_VALUE));

        return start;
    }

    private void runSimulation(Create start){
        addEvent(start.getDistributionRule().apply(start).calculate(), start::complete);
        simulate(SIM_TIME, this::updateStatistics);
    }

    private void updateStatistics(Double delta){
        test1.updateStatistics(delta);
        test2.updateStatistics(delta);
        test3.updateStatistics(delta);
        test4.updateStatistics(delta);
    }

    private void getResultStatistics() {

        TreeMap<String, Double> statistics = getStatisticResults();
        statistics.put("Середнє завантаження " + test1.getName(), test1.getMeanLoad() / SIM_TIME);
        statistics.put("Середнє завантаження " + test2.getName(), test2.getMeanLoad() / SIM_TIME);
        statistics.put("Середнє завантаження " + test3.getName(), test3.getMeanLoad() / SIM_TIME);
        statistics.put("Середнє завантаження " + test4.getName(), test4.getMeanLoad() / SIM_TIME);
        statistics.put("Середня довжина " + test1.getName(), test1.getMeanQueue() / SIM_TIME);
        statistics.put("Середня довжина " + test2.getName(), test2.getMeanQueue() / SIM_TIME);
        statistics.put("Середня довжина " + test3.getName(), test3.getMeanQueue() / SIM_TIME);
        statistics.put("Середня довжина " + test4.getName(), test4.getMeanQueue() / SIM_TIME);
    }

    private Order arrival(){
        return new Order(getTime());
    }
}
