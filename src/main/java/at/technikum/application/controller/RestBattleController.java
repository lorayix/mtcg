package at.technikum.application.controller;

import at.technikum.application.Battle.Battle;
import at.technikum.application.config.DataSource;
import at.technikum.application.model.UserStats;
import at.technikum.application.repository.PostgresCardRepository;
import at.technikum.application.repository.PostgresUserRepository;
import at.technikum.application.repository.UserRepository;
import at.technikum.application.router.Controller;
import at.technikum.application.router.Route;
import at.technikum.application.router.RouteIdentifier;
import at.technikum.application.util.Pair;
import at.technikum.httpserver.HttpStatus;
import at.technikum.httpserver.RequestContext;
import at.technikum.httpserver.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RestBattleController implements Controller {
    private final List<String> lobby;
    public RestBattleController() {
        lobby = new ArrayList<>();
    }
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
    public Response enterLobby(RequestContext requestContext) throws InterruptedException, IOException {
        String token = requestContext.getToken();
        Response response = new Response();
        synchronized (this){
            if(lobby.contains(token)){
                response.setHttpStatus(HttpStatus.BAD_REQUEST);
                response.setBody("This user is already enqued");
            } else {
                lobby.add(token);
            }
        }
        while((lobby.size() % 2) != 0){
        //wait();
        }
        Battle battle = new Battle(lobby.get(0), lobby.get(1), cardRep, uRepo);
        synchronized (this){
            lobby.remove(1);
            lobby.remove(0);
        }
        String log = battle.start();
        if(log.isEmpty()){
            response.setHttpStatus(HttpStatus.NO_CONTENT);
            response.setBody("Something went wrong");
        } else {
            response.setHttpStatus(HttpStatus.OK);

            response.setBody(log);
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
                this::enterLobby
        ));
        return battleRoutes;
    }
}
