package io.sytac.poker.bluffer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
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
}
