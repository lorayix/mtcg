package at.technikum.application.repository;

import at.technikum.application.model.Card;

import java.util.List;
import java.util.UUID;

public interface CardRepository {
    List<Card> buyPackage(String token, String pid);
    List<Card> showCards(String token);
    List<Card> showDeck(String token);
    int configureDeck(String token, List<Card> cards);
    void createCard(Card card, UUID packageID);
    String getUnusedPackage();

    void changeCardOwner(String cardId, String uid);
}
