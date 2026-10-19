package lab3;

import java.util.function.Supplier;

public class Create extends Element {
    private final Supplier<Order> arrive;

    public Create(Model model, Supplier<Order> arriveFunc) {
        super(model);
        arrive = arriveFunc;
    }

    public void complete() {
        Order o = arrive.get();

        if(o == null) return;

        super.getModel().addEvent(
                super.getDistributionRule().apply(this).calculate(),
                this::complete
        );

        super.checkForSensor(o, EventType.COMPLETE);
        super.complete(o);
        Route next = super.getRouter().route();
        if(next == null) return;
        next.dest().start(o);
    }
}
