package lab3;

import java.util.List;
import java.util.Random;
import java.util.TreeMap;

public class HospitalModel extends Model {
    private final double[] arrivalProbabilities = new double[]{0.5, 0.1, 0.4};
    private final IDistribution arrivalInterval = new Distributions.Exp(15);
    private final IDistribution escortTime = new Distributions.Unif(3, 8);
    private final IDistribution timeErToLab = new Distributions.Unif(2, 5);
    private final IDistribution labRegistrationTime = new Distributions.Erlang(4.5, 3);
    private final IDistribution labAnalysisTime = new Distributions.Erlang(4, 2);

    Create start;
    Process er;
    Process escort;
    Process pathToLab;
    Process labRegistration;
    Process lab;

    private final double SIM_TIME = 1000;
    private final double FIRST_ARRIVAL_TIME = 0.0;

    private final int EXAMINED_PATIENT_TO_HOSPITAL = 1;
    private final int UNEXAMINED_PATIENT_TO_HOSPITAL = 2;
    private final int PATIENT_TO_EXAMINATION = 3;

    private int departedClients = 0;
    private double totalStayTime = 0.0;

    private int intervalsCount = 0;
    private double sumOfArrivalIntervals = 0.0;
    private double startArrivalInterval = 0.0;

    public static void main(String[] args) {
        HospitalModel hospital = new HospitalModel();
        Create start = hospital.build();
        hospital.runSimulation(start);
        hospital.getResultStatistics();
        hospital.showResults();
    }

    private Create build(){
        start = new Create(this, this::arrive);
        er = new Process(this);
        escort = new Process(this);
        pathToLab = new Process(this);
        labRegistration = new Process(this);
        lab = new Process(this);

        Sensor labDeparture = new Sensor(s -> s == EventType.COMPLETE, this::senseLabDeparture);
        Sensor wardDeparture = new Sensor(s -> s == EventType.COMPLETE, this::senseWardDeparture);
        Sensor labArrival = new Sensor(s -> s == EventType.START, this::senseLabArrival);

        start.setName("НАДХОДЖЕННЯ");
        start.setDistributionRule((el) -> arrivalInterval);
        start.setRouter(new ProbabilityRouter())
                .addRoutes(new Route(er, start, (double) 1.0));

        ServiceQueue<Order> erQueue = new ServiceQueue<>(Integer.MAX_VALUE);
        erQueue.setRemoveRule(this::erQueueRule);

        er.setName("ПРИЙМАЛЬНЕ ВІДДІЛЕННЯ");
        er.setDeviceCount(2);
        er.setDistributionRule(this::erDistributionRule);
        er.setRouter(new PriorityRouter(this::erRoutingRule))
                .addRoutes(
                    new Route(pathToLab, er, UNEXAMINED_PATIENT_TO_HOSPITAL),
                    new Route(escort, er, EXAMINED_PATIENT_TO_HOSPITAL)
                );
        er.setQueue(erQueue);

        escort.setName("СУПРОВІД ВІДДІЛЕННЯ - ПАЛАТА");
        escort.setDeviceCount(3);
        escort.setDistributionRule((el) -> escortTime);
        escort.addSensor(wardDeparture);
        escort.setQueue(new ServiceQueue<>(Integer.MAX_VALUE));
        escort.setRouter(new ProbabilityRouter());

        pathToLab.setName("ШЛЯХ ВІДДІЛЕННЯ - ЛАБОРАТОРІЯ");
        pathToLab.setDeviceCount(Integer.MAX_VALUE);
        pathToLab.setQueue(new ServiceQueue<>(Integer.MAX_VALUE));
        pathToLab.setDistributionRule((el) -> timeErToLab);
        pathToLab.setRouter(new PriorityRouter(this::erRoutingRule))
            .addRoutes(
                new Route(labRegistration, pathToLab, UNEXAMINED_PATIENT_TO_HOSPITAL),
                new Route(er, pathToLab, EXAMINED_PATIENT_TO_HOSPITAL)
            );

        labRegistration.setName("ЛАБ. РЕЄСТРАЦІЯ");
        labRegistration.setDistributionRule((el) -> labRegistrationTime);
        labRegistration.setQueue(new ServiceQueue<>(Integer.MAX_VALUE));
        labRegistration.addSensor(labArrival);
        labRegistration.setRouter(new ProbabilityRouter())
            .addRoutes(
                new Route(lab, labRegistration, (double) 1.0)
            );

        lab.setName("ЛАБОРАТОРІЯ");
        lab.setDeviceCount(2);
        lab.setDistributionRule((el) -> labAnalysisTime);
        lab.setRouter(new PriorityRouter(this::labRoutingRule))
            .addRoutes(
                new Route(pathToLab, lab, UNEXAMINED_PATIENT_TO_HOSPITAL)
            );
        lab.addSensor(labDeparture);
        lab.setQueue(new ServiceQueue<>(Integer.MAX_VALUE));

        return start;
    }

    private void runSimulation(Create start) {
        addEvent(start.getDistributionRule().apply(start).calculate(), start::complete);
        simulate(SIM_TIME, this::updateStatistics);
    }

    private void getResultStatistics(){
        double avgLabArrivalInterval = sumOfArrivalIntervals / intervalsCount;
        double avgStayTime = totalStayTime / departedClients;

        TreeMap<String, Double> statistics = getStatisticResults();
        statistics.put("Середній час прибуття в лабораторію", avgLabArrivalInterval);
        statistics.put("Середній час перебування пацієнта в лікарні", avgStayTime);
    }

    private Order arrive(){
        Random random = new Random();
        double result = random.nextDouble();
        int type = 0;
        double cumulative = 0.0;

        for(int i=0; i < arrivalProbabilities.length; i++){
            cumulative += arrivalProbabilities[i];

            if (result < cumulative) {
                type = i + 1;
                break;
            }
        }

        return new Order(type, getTime());
    }

    private void updateStatistics(Double delta){
        er.updateStatistics(delta);
        escort.updateStatistics(delta);
        pathToLab.updateStatistics(delta);
        labRegistration.updateStatistics(delta);
        lab.updateStatistics(delta);
    }

    private IDistribution erDistributionRule(Element el){
        return switch (el.getLastServed().getType()) {
            case EXAMINED_PATIENT_TO_HOSPITAL -> new Distributions.Exp(15);
            case UNEXAMINED_PATIENT_TO_HOSPITAL -> new Distributions.Exp(40);
            case PATIENT_TO_EXAMINATION -> new Distributions.Exp(30);
            default -> null;
        };
    }

    private Route erRoutingRule(List<Route> routes){
        Route result = null;
        Element origin = routes.getFirst().origin();
        Order order = origin.getLastServed();

        for (Route route : routes) {
            if(order.getType() < route.priority()) continue;
            result = route;
            break;
        }

        return result;
    }

    private Order erQueueRule(List<Order> queue){
        for(Order c : queue){
            if(c.getType() != EXAMINED_PATIENT_TO_HOSPITAL) continue;
            queue.remove(c);
            return c;
        }

        return queue.removeFirst();
    }

    private Route labRoutingRule(List<Route> routes){
        Route result = null;
        Element origin = routes.getFirst().origin();
        Order patient = origin.getLastServed();

        for (Route route : routes) {
            if(patient.getType() != route.priority()) continue;
            result = route;
        }

        if(patient.getType() == UNEXAMINED_PATIENT_TO_HOSPITAL)
            patient.setType(patient.getType() - 1);

        return result;
    }

    private void senseLabDeparture(Double activationTime, Order patient){
        if(patient.getType() == EXAMINED_PATIENT_TO_HOSPITAL) return;
        senseWardDeparture(activationTime, patient);
    }

    private void senseLabArrival(Double activationTime, Order patient){
        intervalsCount++;
        double endArrivalInterval = activationTime;
        sumOfArrivalIntervals += endArrivalInterval - startArrivalInterval;
        startArrivalInterval = endArrivalInterval;
    }

    private void senseWardDeparture(Double activationTime, Order patient){
        departedClients++;
        totalStayTime += activationTime - patient.getArrivalTime();
    }
}
