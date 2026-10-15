package lab3;

import java.util.List;

public interface IRouter {
    Route route();
    List<Route> getRoutes();
    void addRoute(Route route);
    void addRoutes(Route... routes);
}
