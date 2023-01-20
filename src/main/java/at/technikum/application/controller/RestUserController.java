package at.technikum.application.controller;

import at.technikum.application.model.Credentials;
import at.technikum.application.model.User;
import at.technikum.application.model.UserData;
import at.technikum.application.router.Controller;
import at.technikum.application.router.Route;
import at.technikum.application.router.RouteIdentifier;
import at.technikum.application.service.UserService;
import at.technikum.application.util.Pair;
import at.technikum.httpserver.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static at.technikum.application.router.RouteIdentifier.routeIdentifier;

public class RestUserController implements Controller {

    private final UserService userService;

    public RestUserController(UserService userService) {
        this.userService = userService;
    }

    public Response register(RequestContext requestContext) {
        System.out.println("Body of register: " + requestContext.getBody());
        Credentials credentials = requestContext.getBodyAs(Credentials.class);
        return register(credentials);
    }

    public Response register(Credentials credentials) {
        User user = userService.findUserByUsername(credentials.getUsername());
        if (user != null) {
            throw new BadRequestException("User with username " + credentials.getUsername() + " already exists");
        } else {
            User user1 = new User(credentials.getUsername(), credentials.getPassword());
            userService.save(user1);
        }
        Response response = new Response();
        response.setHttpStatus(HttpStatus.CREATED);
        response.setBody("User " + credentials.getUsername() + " successfully registered");
        return response;
    }

    public Response login(RequestContext requestContext) {
        Credentials credentials = requestContext.getBodyAs(Credentials.class);
        return login(credentials);
    }

    public Response login(Credentials credentials) {
        User user = userService.findUserByUsername(credentials.getUsername());
        int loginOkay;
        if (user == null) {
            throw new BadRequestException("User with username " + credentials.getUsername() + "doesn't exist");
        } else {
            User user1 = new User(credentials.getUsername(), credentials.getPassword());
            loginOkay = userService.loginUser(user1);
        }
        Response response = new Response();
        if (loginOkay == 0) {
            response.setHttpStatus(HttpStatus.OK);
            response.setBody("Login successful");
        } else {
            response.setHttpStatus(HttpStatus.UNAUTHORIZED);
            response.setBody("Either password or username were incorrect");
        }
        return response;
    }

    @Override
    public List<Pair<RouteIdentifier, Route>> listRoutes() {
        List<Pair<RouteIdentifier, Route>> userRoutes = new ArrayList<>();

        userRoutes.add(new Pair<>(
                routeIdentifier("/users", "POST"),
                this::register
        ));

        userRoutes.add(new Pair<>(
                routeIdentifier("/sessions", "POST"),
                this::login
        ));

        userRoutes.add(new Pair<>(
                routeIdentifier("/users", "GET"),
                this::tokenValidation
        ));

        userRoutes.add(new Pair<>(
                routeIdentifier("/users", "PUT"),
                this::tokenValidation
        ));

        return userRoutes;
    }

    public Response tokenValidation(RequestContext requestContext) throws JsonProcessingException {
        String token = requestContext.getToken();
        String username = requestContext.getIdentifier();
        User user = userService.findUserByUsername(username);
        Response response = new Response();
        if (user == null) {
            response.setHttpStatus(HttpStatus.UNAUTHORIZED);
            response.setBody("User hasn't been found");
        } else {
            if (token.contains(username)) {
                System.out.println(requestContext.getHttpVerb());
                if (requestContext.getHttpVerb().equals("GET")) {
                    response = getData(username, token);
                } else if (requestContext.getHttpVerb().equals("PUT")) {
                    response = updateData(token, requestContext);
                }

            } else {
                response.setHttpStatus(HttpStatus.UNAUTHORIZED);
                response.setBody("User token doesn't qualify");
            }
        }
        return response;
    }

    private Response getData(String username, String token) throws JsonProcessingException {
        Response response = new Response();
        UserData userData = userService.getUserData(username, token);
        response.setHttpStatus(HttpStatus.OK);
        String json = new ObjectMapper().writeValueAsString(new UserData(userData.getName(), userData.getBio(), userData.getImage()));
        response.setBody(json);

        return response;
    }

    private Response updateData(String token, RequestContext requestContext) {
        Response response = new Response();
        UserData userData = requestContext.getBodyAs(UserData.class);
        int success = userService.updateData(token, userData);
        if (success == 0) {
            response.setHttpStatus(HttpStatus.OK);
            response.setBody("Data updated");
        } else {
            response.setHttpStatus(HttpStatus.BAD_REQUEST);
            response.setBody("Data couldn't be updated. Please check if you're logged in");
        }
        return response;
    }
}