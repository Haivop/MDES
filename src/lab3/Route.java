package lab3;

import java.util.function.Consumer;

public class Route{
    private Process dest;
    private Element origin;
    private int priority;
    private double probability;
    private boolean isBlocked;
    private Consumer<Route> updateBlock;

    public Route(Process dest, Element origin) {
        this.dest = dest;
        this.origin = origin;
        this.probability = 0;
        this.priority = 0;
        this.isBlocked = false;
        this.updateBlock = null;
    }

    public Route(Process dest, Element origin, double probability) {
        this.dest = dest;
        this.origin = origin;
        this.probability = probability;
        this.priority = 0;
        this.isBlocked = false;
        this.updateBlock = null;
    }

    public Route(Process dest, Element origin, int priority) {
        this.dest = dest;
        this.origin = origin;
        this.probability = 0;
        this.priority = priority;
        this.isBlocked = false;
        this.updateBlock = null;
    }

    public int priority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public double probability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public Process dest() {
        return dest;
    }

    public void setDest(Process dest) {
        this.dest = dest;
    }

    public Element origin() {
        return origin;
    }

    public void setOrigin(Element origin) {
        this.origin = origin;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public Consumer<Route> updateBlock() {
        return updateBlock;
    }

    public void setUpdateBlock(Consumer<Route> updateBlock) {
        this.updateBlock = updateBlock;
    }
}
