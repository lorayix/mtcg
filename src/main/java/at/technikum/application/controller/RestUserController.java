package at.technikum.application.controller;

import at.technikum.application.model.Credentials;
import at.technikum.application.model.User;
import at.technikum.application.repository.UserRepository;
import at.technikum.httpserver.HttpStatus;
import at.technikum.httpserver.Response;

public class RestUserController {

    private final UserRepository userRepository;

    public RestUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Response register(Credentials credentials) {
        User user = userRepository.findUserByUsername(credentials.getUsername());
        if (user != null) {
            Response response = new Response();
            response.setHttpStatus(HttpStatus.BAD_REQUEST);
            response.setBody("User with username " + credentials.getUsername() + " already exists");
            return response;
        }
        Response response = new Response();
        response.setHttpStatus(HttpStatus.CREATED);
        return response;
    }

    public Response login(Credentials credentials) {
        return null;
    }
}
