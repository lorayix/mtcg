package at.technikum.application.controller;

import at.technikum.application.service.BattleService;
import at.technikum.application.config.DataSource;
import at.technikum.application.model.UserStats;
import at.technikum.application.repository.PostgresCardRepository;
import at.technikum.application.repository.PostgresUserRepository;
import at.technikum.application.repository.UserRepository;
import at.technikum.application.router.Controller;
import at.technikum.application.router.Route;
import at.technikum.application.router.RouteIdentifier;
import at.technikum.application.util.Pair;
import at.technikum.httpserver.BadRequestException;
import at.technikum.httpserver.HttpStatus;
import at.technikum.httpserver.RequestContext;
import at.technikum.httpserver.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class RestBattleController implements Controller {
    private static final List<String> lobby = new ArrayList<>();
    static final Object monitor = new Object();

    public RestBattleController() { }
    PostgresCardRepository cardRep = new PostgresCardRepository(DataSource.getInstance());
    UserRepository uRepo = new PostgresUserRepository(DataSource.getInstance());
    private String statsToString(UserStats stats) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(new UserStats(stats.getName(), stats.getElo(), stats.getWins(), stats.getLosses()));
    }
    public Response getScoreboard(RequestContext requestContext) throws JsonProcessingException {
        Response response = new Response();
        List<UserStats> scores = uRepo.getScoreboard();
        response.setBody("");
        for(UserStats stat : scores){
            String json = statsToString(stat);
            response.setBody(response.getBody() + json + "\n");
        }
        response.setHttpStatus(HttpStatus.OK);
        return response;
    }
    public Response getStats(RequestContext requestContext) throws JsonProcessingException {
        String token = requestContext.getToken();
        UserStats userStats = uRepo.getUserScore(token);
        Response response = new Response();
        response.setBody(statsToString(userStats));
        response.setHttpStatus(HttpStatus.OK);
        return response;
    }

    public Response enterFight(RequestContext requestContext) throws InterruptedException {
        String token = requestContext.getToken();
        Response response = new Response();
        BattleService battle;
        String log;

        synchronized(monitor){
            if(lobby.contains(token)){
                throw new BadRequestException("User " + token.replace("-mtcgToken", "") + " has already entered the battle queue");
            }  else {
                lobby.add(token);
            }
            if(lobby.size() % 2 != 0){
                monitor.wait();
            } else {
                monitor.notify();
            }
            battle = new BattleService(lobby.get(0), lobby.get(1), cardRep, uRepo);
            log = battle.start();
        }

        if(log.isEmpty()){
            response.setHttpStatus(HttpStatus.NO_CONTENT);
            response.setBody("Something went wrong");
        } else {
            response.setHttpStatus(HttpStatus.OK);
            response.setBody(log);
        }

        synchronized (monitor){
            lobby.remove(1);
            lobby.remove(0);
        }
        return response;
    }

    @Override
    public List<Pair<RouteIdentifier, Route>> listRoutes() {
        List<Pair<RouteIdentifier, Route>> battleRoutes = new ArrayList<>();

        battleRoutes.add(new Pair<>(
                RouteIdentifier.routeIdentifier("/score", "GET"),
                this::getScoreboard
        ));

        battleRoutes.add(new Pair<>(
                RouteIdentifier.routeIdentifier("/stats", "GET"),
                this::getStats
        ));

        battleRoutes.add(new Pair<>(
                RouteIdentifier.routeIdentifier("/battles", "POST"),
                this::enterFight
        ));
        return battleRoutes;
    }
}
