package lab1;

public class Interval {
    private double start;
    private double end;
    private int frequency;

    public Interval(double start, double end, int frequency) {
        this.start = start;
        this.end = end;
        this.frequency = frequency;
    }

    public void incrementFrequency(){
        this.frequency++;
    }

    public double getEnd(){
        return this.end;
    }

    public double getStart(){
        return this.start;
    }

    public int getFrequency(){
        return this.frequency;
    }

    public void setFrequency(int f){
        this.frequency = f;
    }

    public void setEnd(double newEnd){
        this.end = newEnd;
    }
}
