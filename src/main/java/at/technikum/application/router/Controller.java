package at.technikum.application.router;

import at.technikum.application.util.Pair;

import java.util.List;

public interface Controller {
    List<Pair<RouteIdentifier, Route>> listRoutes();
}
