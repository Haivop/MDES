package lab3;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ServiceQueue<T> {
    private final List<T> queue = new ArrayList<>();
    private final int capacity;
    private int initialLength = 0;
    private String structure = "FIFO";
    private Consumer<T> addFunction;
    private Function<List<T>, T> removeRule;

    public ServiceQueue(int capacity) {
        this.capacity = capacity;
        addFunction = this::addFIFO;
    }

    public ServiceQueue(int capacity, int initialLength) {
        this.capacity = capacity;
        this.initialLength = initialLength;
        addFunction = this::addFIFO;
    }

    public ServiceQueue(int capacity, String structure) {
        this.capacity = capacity;
        switch (structure) {
            case "FIFO":
                addFunction = this::addFIFO;
                break;
            case "LIFO":
                addFunction = this::addLIFO;
                break;
            default: throw new IllegalArgumentException("Invalid structure");
        }
    }

    public ServiceQueue(int capacity, int initialLength, String structure) {
        this.capacity = capacity;
        this.initialLength = initialLength;
    }

    public int getSize() {
        return queue.size();
    }

    public void add(T item) {
        if(queue.size() >= capacity) return;
        addFunction.accept(item);
    }

    public void addLIFO(T item) {
        queue.addFirst(item);
    }

    public void addFIFO(T item) {
        queue.add(item);
    }

    public T remove() {
        if(queue.isEmpty()) return null;
        if (removeRule != null) return removeByRule();
        else return queue.removeFirst();
    }

    public T removeByRule() {
        return removeRule.apply(queue);
    }

    public void remove(T item) {
        queue.remove(item);
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public T getFirst() {
        return queue.getFirst();
    }

    public T getLast() {
        return queue.getLast();
    }

    public int getCapacity() {
        return capacity;
    }

    public int getInitialLength() {
        return initialLength;
    }

    public void clear(){
        queue.clear();
    }

    public String getStructure() {
        return structure;
    }

    public Function<List<T>, T> getRemoveRule() {
        return removeRule;
    }

    public void setRemoveRule(Function<List<T>, T> removeRule) {
        this.removeRule = removeRule;
    }
}
