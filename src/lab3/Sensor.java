package lab3;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class Sensor {
    private Predicate<EventType> activationCondition;
    private BiConsumer<Double, Order> action;

    public Sensor(Predicate<EventType> activationCondition, BiConsumer<Double, Order> action) {
        this.activationCondition = activationCondition;
        this.action = action;
    }

    public Predicate<EventType> getActivationCondition() {
        return activationCondition;
    }

    public BiConsumer<Double, Order> getAction() {
        return action;
    }

    public void setCondition(Predicate<EventType> condition) {
        this.activationCondition = condition;
    }

    public void setAction(BiConsumer<Double, Order> action) {
        this.action = action;
    }
}
