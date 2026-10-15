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
        super.checkForSensor(o, EventType.COMPLETE);
        super.complete(o);
        super.getModel().addEvent(
                super.getDistributionRule().apply(this).calculate(),
                this::complete
        );

        Route next = super.getRouter().route();
        if(next == null) return;
        super.getModel().addEvent(
                0,
                () -> next.dest().start(o)
        );
    }
}
