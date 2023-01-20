package at.technikum.application.router;

import at.technikum.httpserver.RequestContext;
import at.technikum.httpserver.Response;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

public interface Route {
    Response process(RequestContext requestContext) throws IOException;

}

