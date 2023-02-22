package at.technikum.application.Battle;

import at.technikum.application.config.DataSource;
import at.technikum.application.model.Card;
import at.technikum.application.model.CardType;
import at.technikum.application.repository.CardRepository;
import at.technikum.application.repository.PostgresCardRepository;
import at.technikum.application.repository.PostgresUserRepository;
import at.technikum.application.repository.UserRepository;

import java.util.*;


public class Battle {
    public static final int DECKSIZE = 4;
    private final String player1;
    private final String player2;

    private UserRepository userRep;
    private CardRepository cardRep;
    public Battle(String player1, String player2, CardRepository cardRep, UserRepository userRep){
        this.player1 = player1;
        this.player2 = player2;
        this.cardRep = cardRep;
        this.userRep = userRep;
    }
    private Card getRandomCard(List<Card> deck){
        int size = deck.size();
        Random random = new Random();
        int number = random.nextInt(size);
        return deck.get(number);
    }

    public static boolean isStronger(String element1, String element2){
        if((element1 == "Regular" && element2 == "Water") || (element1 == "Fire" && element2 == "Regular") || (element1 == "Water" && element2 == "Fire")){
            return true;
        }
        return false;
    }

    public static Map<String, Float> doubleOrNothing(Card card1, Card card2, boolean dealbreaker){
        Map<String, Float> map = new HashMap<>();
        int modifier = 2;
        String c1_element = card1.returnCardType().getElement();
        String c2_element = card2.returnCardType().getElement();
        Card stronger;
        Card weaker;
        if(isStronger(c1_element, c2_element)){
            stronger = card1;
            weaker = card2;
        } else if (isStronger(c2_element, c1_element)) {
            stronger = card2;
            weaker = card1;
        } else {
            return map;
        }
        if(dealbreaker) {
            modifier = 4;
        }
        map.put(stronger.getName(), stronger.getDamage()*modifier);
        map.put(weaker.getName(), weaker.getDamage()/modifier);
        return map;
    }

    public static boolean isWinner(float c1_damage, float c2_damage, CardType c1, CardType c2){
        if(c1_damage > c2_damage ||
                (c1 == CardType.DRAGON && c2.getType().contains("Goblin")) ||
                (c1 == CardType.WIZZARD && c2 == CardType.ORK) ||
                (c1 == CardType.WATERSPELL && c2 == CardType.KNIGHT) ||
                (c1.getType().contains("Spell") && c2 == CardType.KRAKEN) ||
                (c1 == CardType.FIREELF && c2 == CardType.DRAGON)){
            return true;
        }
        return false;
    }
    public static Card round(Card card1, Card card2, boolean dealbreaker){
        float c1_damage;
        float c2_damage;
        CardType c1 = card1.returnCardType();
        CardType c2 = card2.returnCardType();
        Card loser;

        if(c1.getType().contains("Spell") || c2.getType().contains("Spell") || dealbreaker ){
            Map<String, Float> damageSpecs = doubleOrNothing(card1, card2, dealbreaker);
            if(damageSpecs.size() == 2){
                c1_damage = damageSpecs.get(card1.getName());
                c2_damage = damageSpecs.get(card2.getName());
            } else {
                c1_damage = card1.getDamage();
                c2_damage = card2.getDamage();
            }
        } else {
            c1_damage = card1.getDamage();
            c2_damage = card2.getDamage();
        }
        if(isWinner(c1_damage, c2_damage, c1, c2)){
            loser = card2;
        } else if(isWinner(c2_damage, c1_damage, c2, c1)){
            loser = card1;
        } else {
            loser = null;
        }
        return loser;
    }
    public String start(){
        List<Card> deck_p1 = cardRep.showDeck(player1);
        List<Card> deck_p2 = cardRep.showDeck(player2);
        String log = null;
        if((deck_p2.size() == deck_p1.size()) && deck_p1.size() == DECKSIZE){
            System.out.println("Battle can start, decks have been collected");
            Card losing;
            for(int i = 0; i < 100; i++){
                Card p1_Card = getRandomCard(deck_p1);
                Card p2_Card = getRandomCard(deck_p2);
                if(i % 25 == 0 || deck_p1.size() == 1 || deck_p2.size() == 1){
                    losing = round(p1_Card, p2_Card, true);
                } else {
                    losing = round(p1_Card, p2_Card, false);
                }
                String key = "Round " + i + " = ";
                if(losing == null){
                    log += (key + "This round was without a winner or loser \n");
                } else {
                    String winner;
                    String loser;
                    Card winning;
                    List<Card> winningDeck;
                    List<Card> losingDeck;
                    if(losing == p1_Card){
                        winner = player2;
                        winning = p2_Card;
                        loser = player1;
                        winningDeck = deck_p2;
                        losingDeck = deck_p1;
                    } else {
                        winner = player1;
                        winning = p1_Card;
                        loser = player2;
                        winningDeck = deck_p1;
                        losingDeck = deck_p2;
                    }
                    log += (key + winner.replace("-mtcgToken", "") + " won this round with " + winning.getName() + " - " + losing.getName() + " goes to the winner, until the battle ends or it is won back. \n");
                    losingDeck.remove(losing);
                    winningDeck.add(losing);
                    if(deck_p1.isEmpty() || deck_p2.isEmpty()){
                        log += (key + winner.replace("-mtcgToken", "") + " won this fight after " + i + " rounds");
                        i = 101;
                        userRep.userWin(winner);
                        userRep.userLoss(loser);
                    }
                    if(i == 100){
                        log+=("Inconclusive, neither could finish the other");
                    }
                }
            }
        } else {
            return null;
        }
        return log;
    }
}
