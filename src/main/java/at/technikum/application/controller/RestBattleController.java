package at.technikum.application.controller;

import at.technikum.application.config.DataSource;
import at.technikum.application.model.User;
import at.technikum.application.model.UserData;
import at.technikum.application.model.UserStats;
import at.technikum.application.repository.PostgresUserRepository;
import at.technikum.application.repository.UserRepository;
import at.technikum.application.router.Controller;
import at.technikum.application.router.Route;
import at.technikum.application.router.RouteIdentifier;
import at.technikum.application.service.BattleService;
import at.technikum.application.util.Pair;
import at.technikum.httpserver.HttpStatus;
import at.technikum.httpserver.RequestContext;
import at.technikum.httpserver.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class RestBattleController implements Controller {
    private final BattleService battleService;
    public RestBattleController(BattleService battleService) {
        this.battleService = battleService;
    }
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
    public Response enterLobby(RequestContext requestContext){
        return null;
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
