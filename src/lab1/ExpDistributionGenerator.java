package lab1;

import java.util.*;

public class ExpDistributionGenerator {

    private static final int N = 10_000;
    private static final int INTERVALS = 20;
    private static final double ALPHA = 0.05;

    public static void main(String[] args) {
        double[] lambdas = {0.5, 1.0, 2.0, 5.0};

        for (double lambda : lambdas) {

            System.out.println();
            System.out.println("Експоненційний розподіл (lambda = " + lambda + "):");
            System.out.println("------------------------------------------------------");

            double[] x = generateExponential(N, lambda);
            double mean = Calculations.calculateMean(x);
            double variance = Calculations.calculateVariance(x, mean);

            double theoreticalMean = 1.0 / lambda;
            double theoreticalVariance =
                    1.0 / (lambda * lambda);

            Calculations.printMean(mean, theoreticalMean);
            Calculations.printVariance(variance, theoreticalVariance);
            Calculations.calculateAndPrintMinMax(x);

            double estimatedLambda = 1.0 / mean;

            System.out.printf(
                    "Розрахована lambda = %.6f%n",
                    estimatedLambda
            );

            Histogram histogram = Histogram.buildHistogram(x, INTERVALS);
            histogram.printHistogram();

            double chiSquare =
                    Calculations.calculateChiSquareExp(
                            histogram,
                            N,
                            lambda
                    );

            int degreesOfFreedom = histogram.intervals.size() - 1;
            double chiSquareCritical = Calculations.calculateChiSquaredCritical(
                    degreesOfFreedom,
                    ALPHA);

            Calculations.printChiCheckResult(
                    chiSquare,
                    chiSquareCritical,
                    degreesOfFreedom,
                    "експоненційний");
        }
    }

    private static double[] generateExponential(
            int n,
            double lambda) {

        Random random = new Random();
        double[] x = new double[n];

        for (int i = 0; i < n; i++) {
            double xi = random.nextDouble();
            x[i] = -Math.log(xi) / lambda;
        }

        return x;
    }
}
