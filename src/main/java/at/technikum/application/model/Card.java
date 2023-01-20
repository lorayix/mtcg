package at.technikum.application.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

public class Card {

    @JsonProperty("Id")
    private UUID cardId;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Damage")
    private float damage;

    public Card(UUID cardId, String name, float damage){
        this.cardId = cardId;
        this.name = name;
        this.damage = damage;
    }

    public UUID getCardId() {
        return cardId;
    }

    public void setCardId(UUID cardId) {
        this.cardId = cardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }
}
