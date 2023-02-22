package at.technikum.application.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class Card {

    @JsonProperty("Id")
    private UUID cardId;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Damage")
    private float damage;

    public Card(){
    }
    public Card(UUID cardId, String name, float damage){
        this.cardId = cardId;
        this.name = name;
        this.damage = damage;
    }

    public UUID getCardId() {
        return cardId;
    }

    public String getName() {
        return name;
    }

    public float getDamage() {
        return damage;
    }

    public CardType returnCardType() {
        String name = getName().toUpperCase();
        return CardType.valueOf(name);
    }
}
