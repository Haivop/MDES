package lab1;

import java.util.ArrayList;
import java.util.Arrays;

public class Histogram {
    double intervalWidth;
    double max;
    double min;
    ArrayList<Interval> intervals;

    Histogram(
            double intervalWidth,
            double max,
            double min,
            ArrayList<Interval> intervals) {
        this.intervalWidth = intervalWidth;
        this.max = max;
        this.min = min;
        this.intervals = intervals;
    }

    public void printHistogram() {
        System.out.println();
        System.out.println("Гістограма частот:");
        for (Interval interval : intervals) {
            System.out.printf(
                    "[%.4f; %.4f) : %d%n",
                    interval.getStart(),
                    interval.getEnd(),
                    interval.getFrequency()
            );
        }
    }


    public static Histogram buildHistogram(
            double[] x,
            int numberOfIntervals) {

        double max = Arrays.stream(x).max().getAsDouble();
        double min = Arrays.stream(x).min().getAsDouble();

        double intervalWidth = (max - min) / (numberOfIntervals);

        ArrayList<Interval> frequencies = new ArrayList<>();

        for (int i = 0; i < numberOfIntervals; i++) {
            frequencies.add(new Interval(i*intervalWidth, (i+1)*intervalWidth, 0));
        }


        for (double value : x) {
            int interval = (int) ((value - min) / intervalWidth);

            if (interval >= numberOfIntervals) {
                interval = numberOfIntervals - 1;
            }

            frequencies.get(interval).incrementFrequency();
        }

        for (int i = frequencies.size() - 1; i >= 0; i--) {
            Interval interval = frequencies.get(i);
            if (interval.getFrequency() > 5) continue;

            if (i == 0) {
                int index = i+1;
                Interval mergedInterval = frequencies.get(index);
                interval.setEnd(mergedInterval.getEnd());
                interval.setFrequency(interval.getFrequency() + mergedInterval.getFrequency());

                frequencies.set(index, interval);
                frequencies.remove(i);
                continue;
            }
            int index = i-1;
            Interval mergedInterval = frequencies.get(index);
            mergedInterval.setEnd(interval.getEnd());
            mergedInterval.setFrequency(interval.getFrequency() + mergedInterval.getFrequency());

            frequencies.set(index, mergedInterval);
            frequencies.remove(i);
        }

        return new Histogram(intervalWidth, max, min, frequencies);
    }
}