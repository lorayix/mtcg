package at.technikum;

import at.technikum.application.controller.RestUserController;
import at.technikum.application.repository.PostgresUserRepository;
import at.technikum.application.router.Router;
import at.technikum.application.service.UserService;
import at.technikum.httpserver.HttpServer;
import at.technikum.application.config.DataSource;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService(new PostgresUserRepository(DataSource.getInstance()));
        RestUserController restUserController = new RestUserController(userService);

        Router router = new Router();
        restUserController.listRoutes()
                .forEach(router::registerRoute);

        HttpServer server = new HttpServer(router);
        server.start();
    }
}