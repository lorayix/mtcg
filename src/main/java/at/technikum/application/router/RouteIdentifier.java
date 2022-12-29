package at.technikum.application.router;

import at.technikum.httpserver.RequestContext;
import at.technikum.httpserver.Response;

public interface RouteIdentifier {
    Response process(RequestContext requestContext);
}
