package mkr;

import java.util.*;

public class BankSimulation {

    private static final double MEAN_ARRIVAL_TIME = 3.0;
    private static final double OPERATOR_MIN = 1.0;
    private static final double OPERATOR_MAX = 3.0;
    private static final double CASHIER_MIN = 2.0;
    private static final double CASHIER_MAX = 10.0;

    private static final double RETURN_PROBABILITY = 0.15;
    private static final int MAX_CLIENTS = 8;
    private static final int CASHIERS_COUNT = 2;
    private static final double SIMULATION_TIME = 100_000.0;

    private static final Random random = new Random();

    enum EventType {
        ARRIVAL,
        OPERATOR_DONE,
        CASHIER_DONE
    }

    record Customer(double arrivalTime) { }

    record Event(double time, EventType type, Customer customer) implements Comparable<Event> {
        public Event(double time, EventType type){
            this(time, type, null);
        }

        @Override
        public int compareTo(Event other) {
            return Double.compare(this.time, other.time);
        }
    }

    private final PriorityQueue<Event> events =
            new PriorityQueue<>();

    private final Queue<Customer> operatorQueue =
            new ArrayDeque<>();

    private final Queue<Customer> cashierQueue =
            new ArrayDeque<>();

    private int cashierBusy = 0;
    private int operatorBusy = 0;

    private int clientsInSystem = 0;
    private int lostClients = 0;
    private int admittedClients = 0;
    private int completedClients = 0;
    private int returnedClients = 0;

    private double totalTimeInSystem = 0.0;
    private double currentTime = 0.0;

    private double exponential(double mean) {
        return -mean * Math.log(1.0 - random.nextDouble());
    }

    private double uniform(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    private void addEvent(Event e) {
        events.add(e);
    }

    public void simulate() {
        addEvent(new Event(
            currentTime + exponential(MEAN_ARRIVAL_TIME),
            EventType.ARRIVAL
        ));

        while (!events.isEmpty()) {
            Event event = events.poll();

            if (event.time > SIMULATION_TIME) {
                break;
            }

            System.out.println(currentTime);

            currentTime = event.time;

            switch (event.type) {
                case ARRIVAL -> handleArrival();

                case OPERATOR_DONE ->
                        handleOperatorDone(event.customer);

                case CASHIER_DONE ->
                        handleCashierDone(
                                event.customer
                        );
            }
        }

        printResults();
    }

    private void handleArrival() {
        addEvent(new Event(
                currentTime + exponential(MEAN_ARRIVAL_TIME),
                EventType.ARRIVAL
        ));

        if (clientsInSystem >= MAX_CLIENTS) {
            lostClients++;
            return;
        }

        Customer customer = new Customer(currentTime);

        clientsInSystem++;
        admittedClients++;

        operatorQueue.add(customer);
        startOperator();
    }

    private void startOperator() {
        if (operatorBusy >= 1 || operatorQueue.isEmpty()) {
            return;
        }

        Customer customer = operatorQueue.poll();
        operatorBusy++;

        double serviceTime =
                uniform(OPERATOR_MIN, OPERATOR_MAX);

        addEvent(new Event(
            currentTime + serviceTime,
            EventType.OPERATOR_DONE,
            customer
        ));
    }

    private void handleOperatorDone(Customer customer) {
        operatorBusy--;

        cashierQueue.add(customer);

        startOperator();
        startCashiers();
    }

    private void startCashiers() {
        if (cashierBusy == 2 || cashierQueue.isEmpty()) {
            return;
        }

        Customer customer = cashierQueue.poll();
        cashierBusy++;

        double serviceTime =
                uniform(CASHIER_MIN, CASHIER_MAX);

        addEvent(new Event(
                currentTime + serviceTime,
                EventType.CASHIER_DONE,
                customer
        ));
    }

    private void handleCashierDone(Customer customer) {
        cashierBusy--;

        if (random.nextDouble() < RETURN_PROBABILITY) {
            returnedClients++;
            operatorQueue.add(customer);
            startOperator();
        } else {
            completedClients++;
            totalTimeInSystem +=
                    currentTime - customer.arrivalTime;

            clientsInSystem--;
        }

        startCashiers();
    }

    private void printResults() {
        System.out.println("===== РЕЗУЛЬТАТИ МОДЕЛЮВАННЯ =====");

        System.out.println("Час моделювання: "
                + SIMULATION_TIME + " хв");

        System.out.println("Прийнято клієнтів: "
                + admittedClients);

        System.out.println("Втрачено через відмову: "
                + lostClients);

        System.out.println("Клієнтів завершили обслуговування: "
                + completedClients);

        System.out.println("Повернень до оператора: "
                + returnedClients);

        if (completedClients > 0) {
            System.out.printf(
                    Locale.US,
                    "Середній час перебування клієнта "
                            + "у відділенні: %.3f хв%n",
                    totalTimeInSystem / completedClients
            );
        } else {
            System.out.println(
                    "Немає завершених обслуговувань."
            );
        }

        System.out.println("Клієнтів у системі наприкінці: "
                + clientsInSystem);
    }

    public static void main(String[] args) {
        new BankSimulation().simulate();
    }
}
