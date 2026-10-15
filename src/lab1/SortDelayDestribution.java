package lab1;

import java.util.*;

public class SortDelayDestribution {
    private final static int N = 1_000_000;
    private final static int EXPERIMENTS_N = 150;
    private final static int INTERVALS = 20;
    private static final double ALPHA = 0.05;

    public static void main(String[] args) {
        double[] delays = getDelays();

        double mean = Arrays.stream(delays)
                .average()
                .orElse(0);
        double variance = Arrays.stream(delays)
                .map(t -> Math.pow(t - mean, 2))
                .average()
                .orElse(0);
        Calculations.calculateAndPrintMinMax(delays);
        double stdDev = Math.sqrt(variance);

        Calculations.printMean(mean, -1);
        Calculations.printVariance(variance, -1);

        System.out.println("Середньоквадратичне відхилення: " + stdDev);

        Histogram histogram = Histogram.buildHistogram(delays, INTERVALS);
        histogram.printHistogram();

        double chiSquare = Calculations.calculateChiSquareNormal(
                histogram.intervals,
                mean,
                stdDev);

        int degreesOfFreedom = histogram.intervals.size() - 1;
        double chiSquareCritical = Calculations.calculateChiSquaredCritical(
                degreesOfFreedom,
                ALPHA);

        Calculations.printChiCheckResult(
                chiSquare,
                chiSquareCritical,
                degreesOfFreedom,
                "нормальний");

        System.out.println("Імітація часової затримки (за Нормальним розподілом Гауса):");
        Random rand = new Random();
        double[] simDelays = new double[200];
        for (int k = 0; k < 200; k++) {
            double sumZeta = 0;
            for (int i = 0; i < 12; i++) {
                sumZeta += rand.nextDouble();
            }
            double simulatedTime = stdDev * (sumZeta - 6.0) + mean;
            if (simulatedTime < 0) simulatedTime = 0;
            simDelays[k] = simulatedTime;

            System.out.printf("Симуляція %2d: Згенерована затримка = %.2f мс %n", k+1, simulatedTime);
        }

        double simMean = Calculations.calculateMean(simDelays);
        double simVariance = Calculations.calculateVariance(simDelays, simMean);
        double simStdDev = Math.sqrt(simVariance);

        System.out.println();
        System.out.println("Результати Симуляції");
        System.out.println("--------------------------------------------------------------------------");
        System.out.println();

        Calculations.calculateAndPrintMinMax(simDelays);
        Calculations.printMean(simMean, -1);
        Calculations.printVariance(simVariance, -1);

        System.out.println("Середньоквадратичне відхилення: " + simStdDev);

        Histogram histogramSim = Histogram.buildHistogram(delays, INTERVALS);

        double chiSquareSim = Calculations.calculateChiSquareNormal(
                histogramSim.intervals,
                mean,
                stdDev);

        int degreesOfFreedomSim = histogramSim.intervals.size() - 1;
        double chiSquareCriticalSim = Calculations.calculateChiSquaredCritical(
                degreesOfFreedomSim,
                ALPHA);

        Calculations.printChiCheckResult(
                chiSquareSim,
                chiSquareCriticalSim,
                degreesOfFreedom,
                "нормальний");
    }

    private static double[] getDelays(){
        Random rand = new Random();
        double[] delays = new double[EXPERIMENTS_N];
        List<Integer> dummy = new ArrayList<>();

        for(int i = 0; i < EXPERIMENTS_N; i++){
            for (int j = 0; j < N; j++) {
                dummy.add(rand.nextInt());
            }

            long start = System.nanoTime();
            Collections.sort(dummy);
            long elapsed = System.nanoTime() - start;

            delays[i] = (double) elapsed / 1_000_000;

            System.out.println(i + "done");
        }

        return delays;
    }
}
