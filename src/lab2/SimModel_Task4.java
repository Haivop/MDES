package lab2;

import java.util.ArrayList;

public class SimModel_Task4 {
    static final Task4_Data[] data = {
            new Task4_Data(1.0, 1, 3, 1.0, 1, 3, 1.0, 1, 3, 1.0),

            // Зміна інтервалу надходження
            new Task4_Data(0.8, 1, 3, 1.0, 1, 3, 1.0, 1, 3, 1.0),

            // Зміна кількості пристроїв P1
            new Task4_Data(1.0, 2, 3, 1.0, 1, 3, 1.0, 1, 3, 1.0),

            // Зміна обмеження черги P1
            new Task4_Data(1.0, 1, 5, 1.0, 1, 3, 1.0, 1, 3, 1.0),

            // Зміна середнього часу обробки P1
            new Task4_Data(1.0, 1, 3, 0.5, 1, 3, 1.0, 1, 3, 1.0),

            // Зміна кількості пристроїв P2
            new Task4_Data(1.0, 1, 3, 1.0, 2, 3, 1.0, 1, 3, 1.0),

            // Зміна середнього часу обробки P3
            new Task4_Data(1.0, 1, 3, 1.0, 1, 3, 1.0, 1, 3, 1.8),

            // Зміна обмеження черги P2 та кількості пристроїв P3
            new Task4_Data(1.0, 1, 3, 1.0, 1, 2, 1.0, 3, 3, 1.0),

            // Зміна середнього часу обробки P2 та обмеження черги P3
            new Task4_Data(1.0, 1, 3, 1.0, 1, 3, 1.3, 1, 1, 1.0),

            // Зміна інтервалу надходження, кількості пристроїв P1 та обмеження черги P2
            new Task4_Data(0.7, 2, 3, 1.0, 1, 2, 1.0, 1, 3, 1.0)
    };

    public static void main(String[] args) {
        int i = 0;
        for (Task4_Data run : data) {
            i++;
            Element.reset();

            Create c = new Create(run.c_delay());
            c.setName("CREATOR");
            c.setDistribution("exp");


            Process p1 = new Process(
                    run.p1_delay(),
                    run.p1_devicesCount()
            );
            p1.setMaxqueue(run.p1_maxQueue());
            p1.setName("PROCESSOR_1");
            p1.setDistribution("exp");


            Process p2 = new Process(
                    run.p2_delay(),
                    run.p2_devicesCount()
            );
            p2.setMaxqueue(run.p2_maxQueue());
            p2.setName("PROCESSOR_2");
            p2.setDistribution("exp");


            Process p3 = new Process(
                    run.p3_delay(),
                    run.p3_devicesCount()
            );
            p3.setMaxqueue(run.p3_maxQueue());
            p3.setName("PROCESSOR_3");
            p3.setDistribution("exp");

            c.setNextElement(p1);
            p1.setNextElement(p2);
            p2.setNextElement(p3);

            ArrayList<Element> list = new ArrayList<>();

            list.add(c);
            list.add(p1);
            list.add(p2);
            list.add(p3);

            Model model = new Model(list);

            System.out.println(
                    "\n================ RUN " + i + " ================\n"
            );

            model.simulate(1000.0);
        }
    }
}
