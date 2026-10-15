package lab2;

public class Element {
    private int id;
    private String name;
    private double tnext;
    private double tcurr;
    private final double delayMean;
    private double delayDev;
    private String distribution;
    private int quantity;
    private int state;
    private Element nextElement;
    private static int nextId=0;

    public Element(){
        tnext = Double.MAX_VALUE;
        delayMean = 1.0;
        distribution = "exp";
        tcurr = tnext;
        state=0;
        nextElement=null;
        id = nextId;
        nextId++;
        name = "element"+id;
    }

    public Element(double delay){
        tnext = Double.MAX_VALUE;
        delayMean = delay;
        distribution = "";
        tcurr = tnext;
        state=0;
        nextElement=null;
        id = nextId;
        nextId++;
        name = "element"+id;
    }

    public Element(String nameOfElement, double delay){
        name = nameOfElement;
        tnext = 0.0;
        delayMean = delay;
        distribution = "exp";
        tcurr = tnext;
        state=0;
        nextElement=null;
        id = nextId;
        nextId++;
    }

    public double getDelay() {
        return switch (getDistribution()) {
            case "exp" -> FunRand.Exp(getDelayMean());
            case "norm" -> FunRand.Norm(getDelayMean(), getDelayDev());
            case "unif" -> FunRand.Unif(getDelayMean(), getDelayDev());
            default -> getDelayMean();
        };
    };

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public double getDelayDev() {
        return delayDev;
    }
    public double getDelayMean() {
        return delayMean;
    }
    public String getDistribution() {
        return distribution;
    }
    public int getQuantity() {
        return quantity;
    }
    public int getState() {
        return state;
    }
    public Element getNextElement() {
        return nextElement;
    }
    public double getTcurr() {
        return tcurr;
    }
    public double getTnext() {
        return tnext;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }
    public void setState(int state) {
        this.state = state;
    }
    public void setNextElement(Element nextElement) {
        this.nextElement = nextElement;
    }
    public void setTcurr(double tcurr) {
        this.tcurr = tcurr;
    }
    public void setTnext(double tnext) {
        this.tnext = tnext;
    }

    public void inAct() {
    }
    public void outAct(){
        quantity++;
    }

    public void printResult(){
        System.out.println(getName()+ " quantity = "+ quantity);
    }
    public void printInfo(){
        System.out.print("\n" + getName()+ "\t\ts= " + state +
                "\tq= " + quantity +
                "\tt_next= " + tnext);
    }
    public void updateStatistics(double delta){
    }
    public static void reset(){
        nextId = 0;
    }
}
