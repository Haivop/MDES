package lab3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProbabilityRouter implements IRouter {
    private final Random rand = new Random();
    private final List<Route> routes = new ArrayList<>();

    public ProbabilityRouter() {
        ensureConsistency();
    }

    private void ensureConsistency(){
        double probability = 0;
        for (Route r : getRoutes()) {
            probability += r.probability();

            if(probability >= 1.0) throw new ArithmeticException("Probability overflow");
        }
    }

    @Override
    public Route route() {
        Route result = null;
        double probability = 0;
        double chance = rand.nextDouble();

        for (Route r : getRoutes()) {
            if(r.updateBlock() != null) r.updateBlock().accept(r);
            if(r.isBlocked()) continue;

            probability += r.probability();

            if(chance > probability) continue;
            result = r;
            break;
        }

        return result;
    }

    @Override
    public List<Route> getRoutes() {
        return this.routes;
    }

    @Override
    public void addRoute(Route route) {
        this.routes.add(route);
    }

    @Override
    public void addRoutes(Route... routes) {
        this.routes.addAll(List.of(routes));
    }
}
