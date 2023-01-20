package at.technikum.application.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class TradingDeal {
    @JsonProperty("ID")
    private UUID dealID;
    @JsonProperty("CardToTrade")
    private UUID cardID;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("MinimumDamage")
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
