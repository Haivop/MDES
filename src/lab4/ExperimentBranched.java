package lab4;

import java.util.Map;
import java.util.TreeMap;

public class ExperimentBranched {
    private final static int[] SERVICES_NUM = new int[]{100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};
    private final static int REPETITIONS_NUM = 10;
    private static final double SIM_TIME = 10_000;
    private final Map<String, Double> statistics = new TreeMap<>();

    public static void main(String[] args) {
        ExperimentBranched experiment = new ExperimentBranched();
        for (int k : SERVICES_NUM) {
            BranchedModel expModel = new BranchedModel(k, SIM_TIME, 2);
            expModel.runSimulation();

            for (int i = 0; i < REPETITIONS_NUM; i++) {
                expModel = new BranchedModel(k, SIM_TIME, 2);

                long start = System.nanoTime();
                expModel.runSimulation();
                long elapsed = System.nanoTime() - start;

                expModel.timeToSimulate += elapsed;
                expModel.getResultStatistics().forEach((key, value) ->
                        experiment.statistics.merge(key, value, Double::sum)
                );
            }

            System.out.printf("\n ------------------RESULTS(T=%.0f; N=%d; R=%d)----------------%n",
                    SIM_TIME, k, REPETITIONS_NUM);
            experiment.statistics.forEach(
                    (key, value) -> System.out.println(
                            key + ": " + (double) value / REPETITIONS_NUM
                    )
            );
        }
    }
}
