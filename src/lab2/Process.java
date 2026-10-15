package lab2;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Process extends Element {
    private int queue;
    private int maxqueue;
    private int failure;
    private final int devicesCount;
    private double meanQueue;
    private double meanLoad;
    private final TreeSet<Double> tNextQueue = new TreeSet<>();
    private final List<Element> nextElements = new ArrayList<>();

    public Process(double delay) {
        super(delay);

        devicesCount = 1;
        queue = 0;
        maxqueue = Integer.MAX_VALUE;
        failure = 0;
        meanQueue = 0.0;
        meanLoad = 0.0;
    }

    public Process(double delay, int devicesCount) {
        super(delay);

        this.devicesCount = devicesCount;
        queue = 0;
        maxqueue = Integer.MAX_VALUE;
        failure = 0;
        meanQueue = 0.0;
        meanLoad = 0.0;
    }

    @Override
    public void inAct() {
        if (super.getState() < devicesCount) {
            super.setState(super.getState() + 1);
            double tnext = super.getTcurr() + super.getDelay();

            tNextQueue.add(tnext);
            super.setTnext(tNextQueue.first());
        } else if (getQueue() < getMaxqueue()) {
                setQueue(getQueue() + 1);
        } else {
            failure++;
        }
    }

    @Override
    public void outAct() {
        super.outAct();
        super.setState(Math.max(super.getState() - 1, 0));
        super.setTnext(Double.MAX_VALUE);

        if (!tNextQueue.isEmpty()) {
            tNextQueue.remove(tNextQueue.first());
        }

        if (getQueue() > 0) {
            setQueue(getQueue() - 1);
            super.setState(super.getState() + 1);
            double tnext = super.getTcurr() + super.getDelay();
            tNextQueue.add(tnext);
        }

        if (!tNextQueue.isEmpty()) super.setTnext(tNextQueue.first());

        if(getNextElements().isEmpty()) return;
        for(Element next : getNextElements()) next.inAct();
    }

    public int getFailure() {
        return failure;
    }
    public int getQueue() {
        return queue;
    }
    public void setQueue(int queue) {
        this.queue = queue;
    }
    public int getMaxqueue() {
        return maxqueue;
    }
    public void setMaxqueue(int maxqueue) {
        this.maxqueue = maxqueue;
    }
    @Override
    public void printInfo() {
        super.printInfo();
        System.out.print("\tfailure = " + this.getFailure());
    }
    @Override
    public void updateStatistics(double delta) {
        meanQueue = getMeanQueue() + (queue * delta);
        meanLoad = getMeanLoad() + (getState() * delta);
    }
    public double getMeanQueue() {
        return meanQueue;
    }
    public double getMeanLoad() {
        return meanLoad;
    }
    @Override
    public void setNextElement(Element next) {
        nextElements.add(next);
    }
    public List<Element> getNextElements() {
        return nextElements;
    }
    public int getDevicesCount(){
        return devicesCount;
    }
}
