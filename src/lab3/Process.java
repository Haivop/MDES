package lab3;

public class Process extends Element {
    private int failure;
    private int devicesCount;
    private double meanQueue;
    private double meanLoad;
    private ServiceQueue<Order> queue;

    public Process(Model model){
        super(model);
        devicesCount = 1;
        failure = 0;
        meanQueue = 0.0;
        meanLoad = 0.0;
    }

    @Override
    public void start(Order order) {
        super.start(order);

        if (getDevicesState() < devicesCount) {
            super.checkForSensor(order, EventType.START);
            super.addActivelyServed(order);
            super.setLastServed(order);
            setDevicesState(getDevicesState() + 1);
            getModel().addEvent(
                    getDistributionRule().apply(this).calculate(),
                    () -> this.complete(order)
            );
        } else if (queue.getSize() < getMaxqueue()) {
                queue.add(order);
        } else {
            failure++;
        }
    }

    @Override
    public void complete(Order order) {
        super.complete(order);
        super.setLastServed(order);
        super.getActivelyServed().remove(order);
        super.checkForSensor(order, EventType.COMPLETE);
        setDevicesState(Math.max(super.getDevicesState() - 1, 0));

        if (!queue.isEmpty()) {
            Order o = queue.remove();
            getModel().addEvent(
                    (double) 0,
                    () -> this.start(o)
            );
        }

        if(getRouter() == null) return;
        if(getRouter().getRoutes().isEmpty()) return;
        Route next = getRouter().route();

        if(next == null) {
            getModel().setOrdersInSystem(getModel().getOrdersInSystem() - 1);
            return;
        }
        getModel().addEvent(
                (double) 0,
                () -> next.dest().start(order)
        );
    }

    public int getFailure() {
        return failure;
    }
    public int getMaxqueue() {
        return queue.getCapacity();
    }
    public ServiceQueue<Order> getQueue() {
        return queue;
    }

    @Override
    public void printInfo() {
        super.printInfo();
        System.out.print("\tfailure = " + this.getFailure());
    }
    @Override
    public void updateStatistics(double delta) {
        meanQueue = getMeanQueue() + (queue.getSize() * delta);
        meanLoad = getMeanLoad() + (getDevicesState() * delta);
    }
    public double getMeanQueue() {
        return meanQueue;
    }
    public double getMeanLoad() {
        return meanLoad;
    }
    public int getDevicesCount(){
        return devicesCount;
    }

    public void setDeviceCount(int devicesCount) {
        this.devicesCount = devicesCount;
    }

    public void setQueue(ServiceQueue<Order> queue) {
        this.queue = queue;
    }
}
