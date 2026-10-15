package lab2;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private List<Element> list = new ArrayList<Element>();
    double tnext, tcurr;
    int event;

    public Model(List<Element> elements) {
        list = elements;
        tnext = 0.0;
        event = 0;
        tcurr = tnext;
    }

    public void simulate(double time) {
        while (tcurr < time) {
            tnext = Double.MAX_VALUE;

            for (Element e : list) {
                if (!(e.getTnext() < tnext)) continue;
                tnext = e.getTnext();
                event = e.getId();
            }

            if(tnext >= time) break;

            System.out.print("\n------------------------------");
            System.out.println("\n\n" + list.get(event).getName() +
                    ": t = " + tnext);

            for (Element e : list) {
                e.updateStatistics(tnext - tcurr);
            }

            tcurr = tnext;

            for (Element e : list) {
                e.setTcurr(tcurr);
            }

            list.get(event).outAct();

            for (Element e : list) {
                if(e.getId() == event) continue;
                if (e.getTnext() != tcurr) continue;
                e.outAct();
            }

            printInfo();
        }
        printResult();
    }

    public void printInfo() {
        for (Element e : list) {
            e.printInfo();
        }
    }

    public void printResult() {
        System.out.println("\n-------------RESULTS-------------");
        for (Element e : list) {
            e.printResult();
            if (!(e instanceof Process p)) continue;

            double meanLoadedCount = p.getMeanLoad() / tcurr;
            System.out.printf("mean length of queue = %.3f \n",
                    p.getMeanQueue() / tcurr);
            if(p.getDevicesCount() > 1) {
                System.out.printf("mean number of loaded devices = %.3f \n",
                        meanLoadedCount);
            }
            System.out.printf("mean load of devices = %.3f \n",
                    meanLoadedCount / p.getDevicesCount());
            System.out.printf("total failures = %d \n",
                    p.getFailure());
            System.out.printf("failure probability = %.5f \n\n",
                    p.getFailure() / (p.getFailure() + (double) p.getQuantity()));

        }
    }
}
