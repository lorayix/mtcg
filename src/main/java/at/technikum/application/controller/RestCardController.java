package at.technikum.application.controller;

import at.technikum.application.config.DataSource;
import at.technikum.application.model.Card;
import at.technikum.application.model.User;
import at.technikum.application.repository.PostgresUserRepository;
import at.technikum.application.router.Controller;
import at.technikum.application.router.Route;
import at.technikum.application.router.RouteIdentifier;
import at.technikum.application.service.CardService;
import at.technikum.application.service.UserService;
import at.technikum.application.util.Pair;
import at.technikum.httpserver.HttpStatus;
import at.technikum.httpserver.RequestContext;
import at.technikum.httpserver.Response;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class RestCardController implements Controller {
    private final CardService cardService;
    private UserService userService = new UserService(new PostgresUserRepository(DataSource.getInstance()));

    public RestCardController(CardService cardService) {
        this.cardService = cardService;
    }

    public Response createPackage(RequestContext requestContext) throws IOException {
        System.out.println("Body to create: " + requestContext.getBody());
        List<Card> cards = getCards(requestContext.getBody());
        System.out.println(cards.get(1).getName());
        String token = requestContext.getToken();
        return createPackage(cards, token);
    }

    List<Card> getCards(String body) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<LinkedHashMap> cardsString = objectMapper.readValue(body, List.class);
        List<Card> cards = new ArrayList<>();
        for (HashMap hashMap: cardsString) {
            Object id = hashMap.get("Id");
            UUID uuid = UUID.fromString(id.toString());
            System.out.println(uuid);
            String name = hashMap.get("Name").toString();
            float damage = Float.parseFloat(hashMap.get("Damage").toString());
            cards.add(new Card(uuid, name, damage));
        }
        System.out.println(cards.get(0).getName());
        return cards;
    }
    private Response createPackage(List<Card> cards, String token){
        Response response = new Response();
        if(!token.equals("admin-mtcgToken")){
            response.setHttpStatus(HttpStatus.UNAUTHORIZED);
            response.setBody("Bad request - not authorized to create new packages");
        } else {
            UUID packageID = UUID.randomUUID();
            int size = 0;
            while(size < cards.size()){
                cardService.createCard(cards.get(size), packageID);
                size++;
            }
                response.setHttpStatus(HttpStatus.CREATED);
                response.setBody("Set successfully created");
        }
        return response;
    }
    Response gainPackage(RequestContext requestContext) throws JsonProcessingException {
        String token = requestContext.getToken();
        Response response = new Response();
        int coins = userService.getCoins(token);
        if(coins >= 5) {
            String pid = cardService.getUnusedPackage();
            List<Card> cardPackage = cardService.buyPackage(token, pid);
            for(Card card : cardPackage){
                cardService.changeCardOwner(card.getCardId().toString(), token);
            }
            userService.subtractCoinsForPackage(5, token);
            response = cardListJson(cardPackage);
        } else {
            response.setHttpStatus(HttpStatus.BAD_REQUEST);
            response.setBody("This user has too few coins");
        }
        return response;
    }
    Response cardListJson(List<Card> cards) throws JsonProcessingException {
        Response response = new Response();
        response.setBody("");
        for (Card card: cards) {
            String json = new ObjectMapper().writeValueAsString(new Card(card.getCardId(), card.getName(), card.getDamage()));
            response.setBody(response.getBody() + "\n" + json);
        }
        response.setHttpStatus(HttpStatus.OK);
        return response;
    }
    public Response showCards(RequestContext requestContext) throws JsonProcessingException {
        String token = requestContext.getToken();
        Response response;
        List<Card> cards = cardService.showCards(token);
        if(cards == null){
            response  = new Response();
            response.setBody("No cards found");
            response.setHttpStatus(HttpStatus.NO_CONTENT);
            return response;
        }
        response = cardListJson(cards);
        return response;
    }

    public Response showDeck(RequestContext requestContext) throws JsonProcessingException {
        String token = requestContext.getToken();
        Response response = new Response();
        List<Card> cards = cardService.showDeck(token);
        if(cards == null){
            response.setBody("No cards found");
            response.setHttpStatus(HttpStatus.NO_CONTENT);
            return response;
        }
        response.setHttpStatus(HttpStatus.OK);
        if(!requestContext.getIdentifier().equals("format=plain")){
            response = cardListJson(cards);
        } else {
            String body = "";
            for(Card card : cards){
                body += card.getCardId().toString() + " " +card.getName() + " " + card.getDamage() + "\n";
            }
            response.setBody(body);
        }
        return response;
    }

    public Response configureDeck(RequestContext requestContext){
        String token = requestContext.getToken();
        Response response = new Response();
        List<String> cards = requestContext.getBodyAs(List.class);
        response.setHttpStatus(HttpStatus.BAD_REQUEST);
        if(cards.size() < 4){
            response.setBody("Too few cards set");
        } else {
            if(cardService.configureDeck(cards, token)){
                cardService.resetDeck(token, cards);
                cardService.configureDeck(cards, token);
                response.setHttpStatus(HttpStatus.OK);
                response.setBody("Deck successfully configured");
            } else {
                response.setBody("Cards do not all belong to user, query failed");
            }
        }
        return response;
    }
    @Override
    public List<Pair<RouteIdentifier, Route>> listRoutes() {
        List<Pair<RouteIdentifier, Route>> cardRoutes = new ArrayList<>();

        cardRoutes.add(new Pair<>(
                RouteIdentifier.routeIdentifier("/packages", "POST"),
                this::createPackage
        ));

        cardRoutes.add(new Pair<>(
                RouteIdentifier.routeIdentifier("/transactions/packages", "POST"),
                this::gainPackage
        ));

        cardRoutes.add(new Pair<>(
                RouteIdentifier.routeIdentifier("/cards", "GET"),
                this::showCards
        ));

        cardRoutes.add(new Pair<>(
                RouteIdentifier.routeIdentifier("/deck", "GET"),
                this::showDeck
        ));

        cardRoutes.add(new Pair<>(
                RouteIdentifier.routeIdentifier("/deck", "PUT"),
                this::configureDeck
        ));


        return cardRoutes;
    }
}
