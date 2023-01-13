package at.technikum;

import at.technikum.application.controller.RestUserController;
import at.technikum.application.repository.InMemoryUserRepository;
import at.technikum.application.router.Router;
import at.technikum.application.service.UserService;
import at.technikum.httpserver.HttpServer;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService(new InMemoryUserRepository());
        RestUserController restUserController = new RestUserController(userService);

        Router router = new Router();
        restUserController.listRoutes()
                .forEach(router::registerRoute);

        HttpServer server = new HttpServer(router);
        server.start();
    }
}