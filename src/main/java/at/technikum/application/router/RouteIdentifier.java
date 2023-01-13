package at.technikum.application.router;

import java.util.Objects;

public class RouteIdentifier {
    private final String path;
    private final String httpVerb;

    public RouteIdentifier(String path, String httpVerb) {
        this.path = path;
        this.httpVerb = httpVerb;
    }

    public static RouteIdentifier routeIdentifier(String path, String httpVerb) {
        return new RouteIdentifier(path, httpVerb);
    }

    public String getPath() {
        return path;
    }

    public String getHttpVerb() {
        return httpVerb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RouteIdentifier routeIdentifier = (RouteIdentifier) o;
        return path.equals(routeIdentifier.path) &&
                httpVerb.equals(routeIdentifier.httpVerb);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, httpVerb);
    }
}
