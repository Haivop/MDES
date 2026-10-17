package lab3;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.function.Consumer;

public class Model {
    double tcurr = 0;
    public final PriorityQueue<Event> events = new PriorityQueue<>();
    private final TreeMap<String, Double> statisticResults = new TreeMap<>();
    private int ordersInSystem = 0;
    private int eventsAdded = 0;
    private int eventsCompleted = 0;

    public void addEvent(double delay, Runnable action) {
        eventsAdded++;
        events.add(new Event(tcurr + delay, action));
    }

    public void simulate(double time, Consumer<Double> updateStatistics) {
        while (!events.isEmpty() && events.peek().time() < time) {
            Event next = events.poll();
            eventsCompleted++;
            updateStatistics.accept(next.time() - tcurr);
            tcurr = next.time();
            next.action().run();
        }
        updateStatistics.accept(time - tcurr);
    }

    public double getTime() {
        return tcurr;
    }

    public TreeMap<String, Double> getStatisticResults() {
        return statisticResults;
    }

    public void showResults(){
        System.out.println("\n -----------------------RESULTS-----------------------");
        for(Map.Entry<String, Double> entry : statisticResults.entrySet()){
            System.out.printf("%s :\t %.5f \n", entry.getKey(), entry.getValue());
        }
    }

    public int getOrdersInSystem() {
        return ordersInSystem;
    }

    public void setOrdersInSystem(int ordersInSystem) {
        this.ordersInSystem = ordersInSystem;
    }
}
