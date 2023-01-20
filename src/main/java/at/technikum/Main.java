package at.technikum;

import at.technikum.application.config.DbConnector;
import at.technikum.application.controller.RestCardController;
import at.technikum.application.controller.RestUserController;
import at.technikum.application.repository.PostgresCardRepository;
import at.technikum.application.repository.PostgresUserRepository;
import at.technikum.application.router.Router;
import at.technikum.application.service.CardService;
import at.technikum.application.service.UserService;
import at.technikum.httpserver.HttpServer;
import at.technikum.application.config.DataSource;

public class Main {
    public static void main(String[] args) {
        DbConnector dataSource = DataSource.getInstance();
        UserService userService = new UserService(new PostgresUserRepository(dataSource));
        CardService cardService = new CardService(new PostgresCardRepository(dataSource));
        RestUserController restUserController = new RestUserController(userService);
        RestCardController restCardController = new RestCardController(cardService);

        Router router = new Router();
        restUserController.listRoutes()
                .forEach(router::registerRoute);
        restCardController.listRoutes().
                forEach(router::registerRoute);

        HttpServer server = new HttpServer(router);

        server.start();
    }
}