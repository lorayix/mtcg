package at.technikum.application.controller;

import at.technikum.application.model.Card;
import at.technikum.application.service.CardService;
import at.technikum.application.service.UserService;
import at.technikum.httpserver.BadRequestException;
import at.technikum.httpserver.HttpStatus;
import at.technikum.httpserver.NotAuthorizedException;
import at.technikum.httpserver.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
public class RestCardControllerTest {
    @Mock
    CardService cardService;

    @InjectMocks
    RestCardController restCardController;

    @Test
    void createPackageWorks(){
        List<Card> cards = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            cards.add(new Card(UUID.randomUUID(), "WaterSpell", 20));
        }
        String token = "admin-mtcgToken";
        Response res = restCardController.createPackage(cards, token);
        assertEquals(HttpStatus.CREATED, res.getHttpStatus());
        assertEquals("Set successfully created", res.getBody());
    }

    @Test
    void createPackageWithoutAdminRightsDoesNotWork(){
        List<Card> cards = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            cards.add(new Card(UUID.randomUUID(), "WaterSpell", 20));
        }
        String token = "blublu-mtcgToken";
        try{
            restCardController.createPackage(cards, token);
            fail();
        } catch (NotAuthorizedException e){
            assertEquals(e.getMessage(), "This user is not authorized to create new packages");
        }
    }

    @Test
    void createPackageWith2CardsDoesNotWork(){
        List<Card> cards = new ArrayList<>();
        for(int i = 0; i < 2; i++){
            cards.add(new Card(UUID.randomUUID(), "WaterSpell", 20));
        }
        String token = "admin-mtcgToken";

        try{
            restCardController.createPackage(cards, token);
            fail();
        } catch (BadRequestException e){
            assertEquals(e.getMessage(), "Package has not the required amount of cards");
        }
    }

}
