package io.sytac.poker.bluffer.model;

import lombok.Data;

@Data
public class Player {
    private String id;
    private String name;
    private int chips;
    private String status;
    private int currentBet;
    private boolean isDealer;
    private boolean isSmallBlind;
    private boolean isBigBlind;
    private boolean isSpectator;

    // Getters and Setters...
}
