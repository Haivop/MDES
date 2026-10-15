package lab1;

public class UnifDistributionGenerator {
    private static final int N = 10_000;
    private static final int INTERVALS = 20;
    private static final double ALPHA = 0.05;
    private static final long Z0 = 1L;

    public static void main(String[] args) {
        long[] a_array = {(long) Math.pow(5, 13), (long) Math.pow(5, 8), (long) Math.pow(5, 4)};
        long[] c_array = {(long) Math.pow(2, 31), (long) Math.pow(2, 24), (long) Math.pow(2, 16)};

        for (int i = 0; i < a_array.length; i++) {
            long a = a_array[i];
            long c = c_array[i];

            System.out.println();
            System.out.println("Рівномірний розподіл");
            System.out.println("------------------------------------------------------");
            System.out.println("a = " + a);
            System.out.println("c = " + c);
            System.out.println("z0 = " + Z0);

            double[] x = generateUnif(a, c);

            double mean = Calculations.calculateMean(x);
            double variance = Calculations.calculateVariance(x, mean);

            System.out.println();

            Calculations.calculateAndPrintMinMax(x);
            Calculations.printMean(mean, 0.5);
            Calculations.printVariance(variance, 1.0 / 12.0);

            Histogram histogram = Histogram.buildHistogram(x, INTERVALS);
            histogram.printHistogram();

            double chiSquare = Calculations.calculateChiSquareUnif(
                    histogram.intervals,
                    N,
                    INTERVALS);

            int degreesOfFreedom = histogram.intervals.size() - 1;
            double chiSquareCritical = Calculations.calculateChiSquaredCritical(
                    degreesOfFreedom,
                    ALPHA);

            Calculations.printChiCheckResult(
                    chiSquare,
                    chiSquareCritical,
                    degreesOfFreedom,
                    "рівномірний");
        }

    }

    private static double[] generateUnif(long a, long c) {
        double[] x = new double[N];
        long z = Z0;

        for (int i = 0; i < N; i++) {
            z = (a * z) % c;
            x[i] = (double) z / c;
        }

        return x;
    }
}
