package at.technikum.application.router;

import java.util.HashMap;
import java.util.Map;

public class Router {
    private Map<RouteIdentifier, Route> routes = new HashMap<>();

    public void registerRouter(RouteIdentifier routeIdentifier, Route route){
        routes.put(routeIdentifier, route);
    }

    public Route findRoute(RouteIdentifier routeIdentifier){
        return routes.get(routeIdentifier);
    }
}
