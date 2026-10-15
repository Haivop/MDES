package lab3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PriorityRouter implements IRouter {
    private final List<Route> routes = new ArrayList<>();
    private final Function<List<Route>, Route> priorityRule;

    public PriorityRouter() {
        this.priorityRule = this::backupPriorityRule;
    }

    public PriorityRouter(Function<List<Route>, Route> priorityRule) {
        this.priorityRule = priorityRule;
    }

    @Override
    public Route route() {
        List<Route> routes = getRoutes();
        Route result = null;
        if(routes.isEmpty()){ return result; }

        result = priorityRule.apply(routes);

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

    private Route backupPriorityRule(List<Route> routes) {
        int bestPriority = Integer.MIN_VALUE;
        Route result = null;

        for(Route r : routes){
            if(r.updateBlock() != null) r.updateBlock().accept(r);
            if(r.isBlocked()) continue;

            int priority = r.priority();
            if(priority < bestPriority) continue;
            result = r;
            bestPriority = priority;
        }

        return result;
    }
}
