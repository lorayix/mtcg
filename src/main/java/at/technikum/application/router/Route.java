package at.technikum.application.router;

import at.technikum.httpserver.RequestContext;
import at.technikum.httpserver.Response;

public interface Route {
    Response process(RequestContext requestContext);

}

