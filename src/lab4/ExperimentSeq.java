package lab4;

import java.util.Map;
import java.util.TreeMap;

public class ExperimentSeq {
    private final static int[] SERVICES_NUM = new int[]{100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};
    private final static int REPETITIONS_NUM = 10;
    private static final double SIM_TIME = 10_000;
    private Map<String, Double> statistics = new TreeMap<>();

    public static void main(String[] args) {
        ExperimentSeq experiment = new ExperimentSeq();
        for (int k : SERVICES_NUM) {
            experiment.statistics.clear();
            SequentialModel expModel = new SequentialModel(k, SIM_TIME);
            expModel.runSimulation();

            for (int i = 0; i < REPETITIONS_NUM; i++) {
                expModel = new SequentialModel(k, SIM_TIME);

                long start = System.nanoTime();
                expModel.runSimulation();
                long elapsed = System.nanoTime() - start;

                expModel.timeToSimulate += elapsed;
                expModel.getResultStatistics().forEach((key, value) ->
                        experiment.statistics.merge(key, value, Double::sum)
                );
            }

            experiment.statistics.replaceAll(
                    (key, value) -> value / REPETITIONS_NUM
            );

            System.out.printf("\n ------------------RESULTS(T=%.0f; N=%d; R=%d)----------------%n",
                    SIM_TIME, k, REPETITIONS_NUM);
            experiment.statistics.forEach(
                    (key, value) -> System.out.println(
                            key + ": " + value
                    )
            );
        }
    }


}
