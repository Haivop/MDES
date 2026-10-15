package lab3;

import java.util.Random;

public class Distributions {
    public static class Erlang implements IDistribution {
        private final double mean;
        private final int k;

        public Erlang(double mean, int k) {
            this.mean = mean;
            this.k = k;
        }

        public double calculate() {
            double a = 0.0;

            for (int i = 0; i < k; i++) {
                double u = Math.random();
                a += -Math.log(u) / mean;
            }

            return a;
        }
    }

    public static class Exp implements IDistribution {
        private final double mean;

        public Exp(double mean){
            this.mean = mean;
        }

        @Override
        public double calculate() {
            double a = 0;
            while (a == 0) {
                a = Math.random();
            }
            a = -mean * Math.log(a);
            return a;
        }
    }

    public static class Norm implements IDistribution {
        private final double mean, deviation;

        public Norm(double mean, double deviation) {
            this.mean = mean;
            this.deviation = deviation;
        }

        public double calculate() {
            double a;
            Random r = new Random();
            a = mean + deviation * r.nextGaussian();
            return a;
        }
    }

    public static class Unif implements IDistribution {
        private final double min;
        private final double max;

        public Unif(double min, double max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public double calculate() {
            double a = 0;
            while (a == 0) {
                a = Math.random();
            }
            a = min + a * (max - min);
            return a;
        }
    }
}
