package io.sytac.poker.bluffer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameState {
    private List<Player> players;
    private List<Card> communityCards;
    private int pot;
    private String currentBettingRound;
    private String currentPlayerId;
    private String gamePhase;
    private String message;
    private int minimumRaiseAmount;
    private int minimumBetForCall;
}
