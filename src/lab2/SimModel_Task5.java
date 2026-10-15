package lab2;

import java.util.ArrayList;

public class SimModel_Task5 {
    public static void main(String[] args) {
        Create c = new Create(1.0);
        Process p1 = new Process(1.0, 3);
        Process p2 = new Process(1.0, 2);
        Process p3 = new Process(1.0);

        c.setName("CREATOR");
        c.setNextElement(p1);
        c.setDistribution("exp");

        p1.setMaxqueue(3);
        p1.setNextElement(p2);
        p1.setName("PROCESSOR_1");
        p1.setDistribution("exp");

        p2.setMaxqueue(3);
        p2.setNextElement(p3);
        p2.setName("PROCESSOR_2");
        p2.setDistribution("exp");

        p3.setMaxqueue(3);
        p3.setName("PROCESSOR_3");
        p3.setDistribution("exp");

        ArrayList<Element> list = new ArrayList<>();
        list.add(c);
        list.add(p1);
        list.add(p2);
        list.add(p3);

        Model model = new Model(list);
        model.simulate(1000.0);
    }
}
