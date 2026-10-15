package lab3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class Element {
    private String name;
    private Model model;
    private double tnext;
    private double tcurr;
    private Function<Element, IDistribution> distributionRule;
    private int quantity;
    private int devicesState=0;
    private IRouter router = null;
    private Order lastServed = null;
    private final List<Order> activelyServed = new ArrayList<>();
    private final List<Sensor> sensors = new ArrayList<>();

    public Element(Model model) {
        this.model = model;
        distributionRule = (type) -> new Distributions.Exp(1);
        router = new ProbabilityRouter();
        tnext = Double.MAX_VALUE;
        tcurr = tnext;
        devicesState=0;
    }

    public String getName() {
        return name;
    }
    public Function<Element, IDistribution> getDistributionRule() {
        return distributionRule;
    }
    public int getQuantity() {
        return quantity;
    }
    public int getDevicesState() {
        return devicesState;
    }

    public double getTcurr() {
        return tcurr;
    }
    public double getTnext() {
        return tnext;
    }
    public IRouter getRouter() {
        return router;
    }
    public Model getModel(){
        return model;
    }
    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setModel(Model model) {
        this.model = model;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDevicesState(int devicesState) {
        this.devicesState = devicesState;
    }
    public IRouter setRouter(IRouter router) {
        this.router = router;
        return this.router;
    }
    public void addSensor(Sensor sensor) {
        this.sensors.add(sensor);
    }

    public void start(Order order) {
    }
    public void complete(Order order) {
        quantity++;
    }

    public void printResult(){
        System.out.println(getName()+ " quantity = "+ quantity);
    }
    public void printInfo(){
        System.out.print("\n" + getName()+ "\t\ts= " + devicesState +
                "\tq= " + quantity +
                "\tt_next= " + tnext);
    }
    public void updateStatistics(double delta){
    }

    public void setDistributionRule(Function<Element, IDistribution> distributionRule) {
        this.distributionRule = distributionRule;
    }

    public Order getLastServed() {
        return lastServed;
    }

    public void setLastServed(Order served) {
        this.lastServed = served;
    }

    public List<Order> getActivelyServed() {
        return activelyServed;
    }

    public void addActivelyServed(Order order) {
        this.activelyServed.add(order);
    }

    public void checkForSensor(Order order, EventType eventType) {
        for(Sensor s : getSensors()) {
            if(!s.getActivationCondition().test(eventType)) continue;
            s.getAction().accept(getModel().getTime(), order);
        }
    }
}
