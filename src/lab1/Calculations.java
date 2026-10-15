//package lab1;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//
//
//public class Calculations {
//    private static String destribution;
//
//    public static double calculateMean(double[] x) {
//        double sum = 0;
//        for (double value : x) sum += value;
//        return sum / x.length;
//    }
//
//
//    public static double calculateVariance(
//            double[] x,
//            double mean) {
//
//        double sum = 0;
//        for (double value : x) sum += Math.pow(value - mean, 2);
//
//        return sum / x.length;
//    }
//
//    public static void calculateAndPrintMinMax(double[] x){
//        double max = Arrays.stream(x).max().getAsDouble();
//        double min = Arrays.stream(x).min().getAsDouble();
//
//        System.out.printf(
//                "max = %.6f ; min = %.6f%n",
//                max, min
//        );
//    }
//
//    public static double calculateChiSquareExp(
//            Histogram histogram,
//            int n,
//            double lambda) {
//
//        double chiSquare = 0;
//        ArrayList<Interval> intervals = histogram.intervals;
//
//        for (int i = 0; i < intervals.size(); i++) {
//            double left = intervals.get(i).getStart();
//            double probability;
//
//            if (i == intervals.size() - 1) {
//                probability = Math.exp(-lambda * left);
//            } else {
//                double right = intervals.get(i).getEnd();
//
//                probability =
//                        Math.exp(-lambda * left) - Math.exp(-lambda * right);
//            }
//
//            double expected = n * probability;
//
//            double observed = intervals.get(i).getFrequency();
//
//            if (expected > 0) {
//                chiSquare += Math.pow(
//                        observed - expected,
//                        2
//                ) / expected;
//            }
//        }
//
//        return chiSquare;
//    }
//
//    public static double calculateChiSquareUnif(
//            ArrayList<Interval> intervals,
//            int N, int K
//    ) {
//        double expected = (double) N / K;
//        double chiSquare = 0.0;
//
//        for (Interval observed : intervals) {
//            double difference = observed.getFrequency() - expected;
//            chiSquare += difference * difference / expected;
//        }
//
//        return chiSquare;
//    }
//
//    public static double calculateChiSquareNormal(
//            ArrayList<Interval> intervals,
//            double mean,
//            double std
//    ) {
//        int k = intervals.size();
//        double n = 0;
//
//        for (Interval interval : intervals) {
//            n += interval.getFrequency();
//        }
//
//        NormalDistribution normal = new NormalDistribution(mean, std);
//        double chiSquare = 0;
//
//        for (int i = 0; i < k; i++) {
//            Interval observed = intervals.get(i);
//
//            double p = normal.cumulativeProbability(observed.getEnd())
//                    - normal.cumulativeProbability(observed.getStart());
//
//            double expected = n * p;
//
//            if (expected >= 5) {
//                chiSquare += Math.pow(observed.getFrequency() - expected, 2)
//                        / expected;
//            }
//        }
//
//        return chiSquare;
//    }
//
//    public static double calculateChiSquaredCritical(int degreesOfFreedom, double alpha){
//        ChiSquaredDistribution distribution =
//                new ChiSquaredDistribution(degreesOfFreedom);
//
//        return distribution.inverseCumulativeProbability(1 - alpha);
//    }
//
//    public static void printMean(double mean, double theoretical){
//        System.out.printf(
//                "Середнє вибірки (теоретичне = %.6f) : %.6f%n",
//                theoretical, mean
//        );
//    };
//
//    public static void printVariance(double variance, double theoretical){
//        System.out.printf(
//                "Дисперсія вибірки (теоретична = %.6f) : %.6f%n",
//                theoretical, variance
//        );
//    }
//
//    public static void printChiCheckResult(
//            double chiSquare,
//            double chiCritical,
//            int degreeOfFreedom,
//            String destribution){
//
//        System.out.println();
//        System.out.println("Перевірка критерієм χ²:");
//
//        System.out.printf(
//                "χ² розрахункове = %.6f%n",
//                chiSquare
//        );
//
//        System.out.printf(
//                "χ² критичне     = %.3f%n",
//                chiCritical
//        );
//
//        System.out.println(
//                "Ступені свободи  = " + degreeOfFreedom
//        );
//
//        if (chiSquare < chiCritical) {
//
//            System.out.println(
//                    "Результат: гіпотеза про " + destribution
//                            + "розподіл НЕ відхиляється."
//            );
//
//        } else {
//            System.out.println(
//                    "Результат: гіпотеза про " + destribution
//                            + "розподіл ВІДХИЛЯЄТЬСЯ."
//            );
//        }
//    }
//
//}
