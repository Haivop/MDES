package lab2;

import java.util.ArrayList;

public class SimModel_Task1and2 {
    public static void main(String[] args) {
        Create c = new Create(1.0);
        Process p1 = new Process(1.0);

        c.setName("CREATOR");
        c.setNextElement(p1);
        c.setDistribution("exp");

        p1.setMaxqueue(3);
        p1.setName("PROCESSOR_1");
        p1.setDistribution("exp");

        ArrayList<Element> list = new ArrayList<>();
        list.add(c);
        list.add(p1);

        Model model = new Model(list);
        model.simulate(1000.0);
    }
}
