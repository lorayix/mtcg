package at.technikum.application.Battle;


import at.technikum.application.model.Card;
import at.technikum.application.model.CardType;
import at.technikum.application.model.User;
import at.technikum.application.repository.CardRepository;
import at.technikum.application.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BattleTest {

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
        Battle battle = new Battle("player1", "player2", cardRepository, userRepository);
        when(cardRepository.showDeck("player1")).thenReturn(deck_p1);
        when(cardRepository.showDeck("player2")).thenReturn(deck_p2);

        String result = battle.start();

        assertEquals(result, null);
    }

    @Test
    void regularIsStrongerThanWater(){
        String element1 = "Regular";
        String element2 = "Water";

        boolean stronger = Battle.isStronger(element1, element2);

        assertEquals(true, stronger);
    }

    @Test
    void waterIsNotStrongerThanRegular(){
        String element1 = "Water";
        String element2 = "Regular";

        boolean stronger = Battle.isStronger(element1, element2);
        assertEquals(false, stronger);
    }

    @Test
    void waterIsNotStrongerThanWater(){
        String element1 = "Water";
        String element2 = "Water";

        boolean stronger = Battle.isStronger(element1, element2);
        assertEquals(false, stronger);
    }

    @Test
    void waterSpellDrownsKnight(){
        float c1_damage = 20;
        float c2_damage = 50;
        CardType c1 = CardType.WATERSPELL;
        CardType c2 = CardType.KNIGHT;

        boolean winner = Battle.isWinner(c1_damage, c2_damage, c1, c2);
        assertEquals(true, winner);
    }

    @Test
    void dragonBeatsGoblins(){

    }
}
