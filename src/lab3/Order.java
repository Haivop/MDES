package lab3;

public class Order {
    private int type;
    private final double arrivalTime;

    public Order(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Order(int type, double arrivalTime) {
        this.type = type;
        this.arrivalTime = arrivalTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }
}
