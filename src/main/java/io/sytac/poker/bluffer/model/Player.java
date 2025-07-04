package io.sytac.poker.bluffer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private String id;
    private String name;
    private int chips;
    private String status;
    private int currentBet;
    @JsonProperty("isDealer")
    private boolean isDealer;
    @JsonProperty("isSmallBlind")
    private boolean isSmallBlind;
    @JsonProperty("isBigBlind")
    private boolean isBigBlind;
    @JsonProperty("isSpectator")
    private boolean isSpectator;
}
