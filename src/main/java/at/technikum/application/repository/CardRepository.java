package at.technikum.application.repository;

import at.technikum.application.model.Card;

import java.util.List;
import java.util.UUID;

public interface CardRepository {
    List<Card> buyPackage(String token, String pid);
    List<Card> showCards(String token);
    List<Card> showDeck(String token);
    boolean configureDeck(List<String> cards, String uid);
    void createCard(Card card, UUID packageID);
    String getUnusedPackage();
    void changeCardOwner(String cardId, String token);

    void resetDeck(String token, List<String> cid);
}
