package at.technikum.application.service;


import at.technikum.application.model.Card;
import at.technikum.application.repository.CardRepository;
import at.technikum.application.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BattleServiceTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void emptyDeckReturnsNull(){
        List<Card> deck_p1 = new ArrayList<>();
        List<Card> deck_p2 = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            deck_p2.add(new Card());
        }
        CardRepository cardRepository = mock(CardRepository.class);
        BattleService battle = new BattleService("player1", "player2", cardRepository, userRepository);
        when(cardRepository.showDeck("player1")).thenReturn(deck_p1);
        when(cardRepository.showDeck("player2")).thenReturn(deck_p2);

        String result = battle.start();

        assertEquals(result, null);
    }

    @Test
    void regularIsStrongerThanWater(){
        String element1 = "Regular";
        String element2 = "Water";

        boolean stronger = BattleService.beats(element1, element2);

        assertEquals(true, stronger);
    }

    @Test
    void waterIsNotStrongerThanRegular(){
        String element1 = "Water";
        String element2 = "Regular";

        boolean stronger = BattleService.beats(element1, element2);
        assertEquals(false, stronger);
    }

    @Test
    void waterIsNotStrongerThanWater(){
        String element1 = "Water";
        String element2 = "Water";

        boolean stronger = BattleService.beats(element1, element2);
        assertEquals(false, stronger);
    }

    @Test
    void waterSpellDrownsKnight(){
        Card card1 = new Card(UUID.randomUUID(), "WaterSpell", 20);
        Card card2 = new Card(UUID.randomUUID(), "Knight", 50);

        Card loser = BattleService.round(card1, card2, false);

        assertEquals(loser, card2);
    }
    @Test
    void knightDrownsByWaterSpell(){
        Card card1 = new Card(UUID.randomUUID(), "Knight", 50);
        Card card2 = new Card(UUID.randomUUID(), "WaterSpell", 20);
        Card loser = BattleService.round(card1, card2, false);

        assertEquals(loser, card1);
    }

    @Test
    void goblinLosesToDragon(){
        Card card1 = new Card(UUID.randomUUID(), "Dragon", 20);
        Card card2 = new Card(UUID.randomUUID(), "FireGoblin", 40);

        Card loser = BattleService.round(card1, card2, false);
        assertEquals(loser, card2);
    }

    @Test
    void dealbreakerWorks(){
        Card card1 = new Card(UUID.randomUUID(), "WaterElf", 10);
        Card card2 = new Card(UUID.randomUUID(), "FireElf", 120);

        Card loser = BattleService.round(card1, card2, true);

        assertEquals(loser, card2);
    }

    @Test
    void higherDamageWinsWithoutDealbreaker(){
        Card card1 = new Card(UUID.randomUUID(), "WaterElf", 10);
        Card card2 = new Card(UUID.randomUUID(), "FireElf", 120);

        Card loser = BattleService.round(card1, card2, false);

        assertEquals(loser, card1);
    }

    @Test
    void stackedDeckWinsBattle() {
        List<Card> deck_p1 = new ArrayList<>();
        List<Card> deck_p2 = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            deck_p1.add(new Card(UUID.randomUUID(), "WaterElf", 50));
            deck_p2.add(new Card(UUID.randomUUID(), "FireElf", 10));
        }
        CardRepository cardRepository = mock(CardRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        BattleService battle = new BattleService("player1-mtcgToken", "player2-mtcgToken", cardRepository, userRepository);
        when(cardRepository.showDeck("player1-mtcgToken")).thenReturn(deck_p1);
        when(cardRepository.showDeck("player2-mtcgToken")).thenReturn(deck_p2);

        String result = battle.start();
        assertTrue(result.contains("player1 won this fight"));
    }
}
