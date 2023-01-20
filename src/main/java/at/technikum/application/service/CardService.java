package at.technikum.application.service;

import at.technikum.application.model.Card;
import at.technikum.application.repository.CardRepository;

import java.util.List;
import java.util.UUID;


public class CardService {
    private final CardRepository cardRepository;
    public CardService(CardRepository cardRepository) {this.cardRepository = cardRepository;}
    public void createCard(Card card, UUID packageID) {
        cardRepository.createCard(card, packageID);
    }
    public void changeCardOwner(String cardId, String token){
        cardRepository.changeCardOwner(cardId, token);
    }
    public List<Card> buyPackage(String token, String pid){
        return cardRepository.buyPackage(token, pid);
    }
    public List<Card> showCards(String token){
        return cardRepository.showCards(token);
    }
    public List<Card> showDeck(String token){
        return cardRepository.showDeck(token);
    }
    public boolean configureDeck(List<String> cards, String uid){
        return cardRepository.configureDeck(cards, uid);
    }
    public void resetDeck(String token, List<String> cid){
        cardRepository.resetDeck(token, cid);
    }
    public String getUnusedPackage(){
        return cardRepository.getUnusedPackage();
    }
}
