package at.technikum.application.model;

import java.util.UUID;

public class Card {

    UUID cardId;
    private String name;
    private float damage;

    Card(UUID cardId, String name, float damage){
        this.cardId = cardId;
        this.name = name;
        this.damage = damage;
    }
}
