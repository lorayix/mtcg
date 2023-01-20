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
    public void changeCardOwner(String cardId, String uid){
        cardRepository.changeCardOwner(cardId, uid);
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
    public int configureDeck(String token, List<Card> cards){
        return cardRepository.configureDeck(token, cards);
    }
    public String getUnusedPackage(){
        return cardRepository.getUnusedPackage();
    }
}
