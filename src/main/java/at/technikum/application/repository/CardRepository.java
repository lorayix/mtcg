package at.technikum.application.repository;

import at.technikum.application.model.Card;
import at.technikum.application.model.TradingDeal;

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

    boolean createTradingDeal(TradingDeal deal, String token);

    List<TradingDeal> getTradingDeals();

    String deleteDeal(String token, UUID tradingID);

    Card getCardFromID(UUID cardID);

    TradingDeal getDealFromID(UUID tradingID);

    String getOwner(String cardID);
}
