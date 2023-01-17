package at.technikum.application.model;

import java.util.UUID;

public class TradingDeal {
    private UUID dealID;
    private UUID cardID;
    private String type;
    private float minDamage;

    public TradingDeal(UUID dealID, UUID cardID, String type, float minDamage) {
        this.dealID = dealID;
        this.cardID = cardID;
        this.type = type;
        this.minDamage = minDamage;
    }

    public UUID getDealID() {
        return dealID;
    }

    public UUID getCardID() {
        return cardID;
    }

    public String getType() {
        return type;
    }

    public float getMinDamage() {
        return minDamage;
    }
}
