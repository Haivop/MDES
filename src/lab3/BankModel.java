package lab3;

import java.util.*;

public class BankModel extends Model {
    private final IDistribution arrivalInterval = new Distributions.Exp(0.5);
    private final IDistribution cashiersWorkTime = new Distributions.Exp(0.3);
    private final IDistribution startingCashiersWorkTime = new Distributions.Norm(1, 0.3);
    private Create start;
    private final Process[] cashiersArr = new Process[2];

    private final double FIRST_CUSTOMER_ARR_TIME = 0.1;
    private final double SIM_TIME = 1000;
    private final int LANES_CAPACITY = 3;
    private final String LANES_STRUCTURE = "FIFO";

    private double avgClientCountAtMoment = 0.0;

    private int intervalsCount = 0;
    private double sumOfDepartureIntervals = 0.0;
    private double startDepartureInterval = 0.0;

    private int departedClients = 0;
    private double totalStayTime = 0.0;

    private int changedLaneCount = 0;

    public static void main(String[] args) {
        BankModel bank = new BankModel();
        Create start = bank.build();
        bank.runSimulation(start);
        bank.getResultStatistics();
        bank.showResults();
    }

    private Create build() {
        start = new Create(this, this::arrival);
        Sensor departureSensor = new Sensor(s -> s == EventType.COMPLETE, this::senseDeparture);
        Sensor allSensor = new Sensor(s -> s == EventType.COMPLETE || s == EventType.START, this::superviseLaneChange);

        Arrays.setAll(cashiersArr, i -> {
            Process cashier = new Process(this);

            cashier.setName("КАСИР " + (i + 1));
            cashier.setDistributionRule((el) -> cashiersWorkTime);
            cashier.addSensor(departureSensor);
            cashier.addSensor(allSensor);
            cashier.setQueue(new ServiceQueue<>(LANES_CAPACITY, LANES_STRUCTURE));

            return cashier;
        });

        start.setName("НАДХОДЖЕННЯ");
        start.setRouter(new PriorityRouter(this::priorityRule))
             .addRoutes(
                new Route(cashiersArr[0], start, 1),
                new Route(cashiersArr[1], start,  0)
             );
        start.setDistributionRule((type) -> arrivalInterval);


        return start;
    }

    private void runSimulation(Create start){
        for(Process cashier : cashiersArr){
            for (int i = 0; i < cashier.getQueue().getInitialLength(); i++)
                cashier.getQueue().add(arrival());
        }

        addEvent(startingCashiersWorkTime.calculate(), () -> cashiersArr[0].complete(arrival()));
        addEvent(startingCashiersWorkTime.calculate(), () -> cashiersArr[1].complete(arrival()));
        addEvent(FIRST_CUSTOMER_ARR_TIME, start::complete);
        simulate(SIM_TIME, this::updateStatistics);
    }

    private void getResultStatistics() {
        double avgDepartureInterval = sumOfDepartureIntervals / intervalsCount;
        double avgStayTime = totalStayTime / departedClients;
        double sumFailure = Arrays.stream(cashiersArr)
                .mapToDouble(Process::getFailure)
                .sum();

        System.out.println(sumFailure + " " + changedLaneCount + " " + sumOfDepartureIntervals + " " + totalStayTime);


        double failurePercentage = sumFailure / start.getQuantity() * 100;

        TreeMap<String, Double> statistics = getStatisticResults();
        statistics.put("Середнє завантаження " + cashiersArr[0].getName(), cashiersArr[0].getMeanLoad() / SIM_TIME);
        statistics.put("Середнє завантаження " + cashiersArr[1].getName(), cashiersArr[1].getMeanLoad() / SIM_TIME);
        statistics.put("Середня кількість клієнтів в черзі 1", cashiersArr[0].getMeanQueue() / SIM_TIME);
        statistics.put("Середня кількість клієнтів в черзі 2", cashiersArr[1].getMeanQueue() / SIM_TIME);
        statistics.put("Середня кількість клієнтів в момент часу", avgClientCountAtMoment / SIM_TIME);
        statistics.put("Середній час між відїздами від касирів", avgDepartureInterval);
        statistics.put("Середній час перебування клієнта в банку", avgStayTime);
        statistics.put("Відсоток відмов:", failurePercentage);
        statistics.put("Кількість змін смуг:", (double) changedLaneCount);
    }

    private Order arrival(){
        return new Order(getTime());
    }

    private void updateStatistics(Double delta){
        for (Process cashier : cashiersArr) {
            cashier.updateStatistics(delta);
        }
        avgClientCountAtMoment += clientCount() * delta;
    }

    private void superviseLaneChange(Double activationTime, Order customer){
        ServiceQueue<Order> firstLane = cashiersArr[0].getQueue();
        ServiceQueue<Order> secondLane = cashiersArr[1].getQueue();

        if(Math.abs(firstLane.getSize() - secondLane.getSize()) < 2) return;

        if(firstLane.getSize() > secondLane.getSize()) changeLane(firstLane, secondLane);
        else if(secondLane.getSize() > firstLane.getSize()) changeLane(secondLane, firstLane);
    }

    private void changeLane(ServiceQueue<Order> to, ServiceQueue<Order> from){
        if(from.isEmpty()) return;
        changedLaneCount++;
        Order last = from.getLast();
        from.remove(last);
        to.add(last);
    }

    private void senseDeparture(Double activationTime, Order customer){
        intervalsCount++;
        double endDepartureInterval = activationTime;
        sumOfDepartureIntervals += endDepartureInterval - startDepartureInterval;
        startDepartureInterval = endDepartureInterval;

        departedClients++;
        totalStayTime += activationTime - customer.getArrivalTime();
    }

    private int clientCount(){
        int count = 0;
        for (Process p : cashiersArr) {
            count += p.getDevicesState();
            count += p.getQueue().getSize();
        }
        return count;
    }

    private Route priorityRule(List<Route> routes){
        int firstSize = (routes.stream().anyMatch(Objects::isNull))
                ? 0
                : routes.getFirst().dest().getQueue().getSize();
        int shortestLength = Integer.MAX_VALUE;
        int bestPriority = 0;
        Route result = null;
        boolean allEqual = true;
        boolean allEmpty = true;


        for(Route r : routes){
            if(r == null) continue;
            ServiceQueue<Order> q = r.dest().getQueue();

            if(q.getSize() < shortestLength){
                result = r;
                shortestLength = q.getSize();
            }

            allEmpty &= q.getSize() == 0;
            allEqual &= q.getSize() == firstSize;
        }

        if(!(allEqual || allEmpty)) return result;

        for(Route r : routes){
            int priority = r.priority();
            if(priority < bestPriority) continue;
            result = r;
            bestPriority = priority;
        }

        return result;
    }
}
