package io.sytac.poker.bluffer.service;

import java.util.List;

import io.sytac.poker.bluffer.model.Card;
import io.sytac.poker.bluffer.model.GameState;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class BotLogic {

    public static Map<String, Object> decideAction(String botName, String id, GameState state, List<Card> hand) {
        String action;
        int callAmount = state.getMinimumBetForCall();
        int raiseAmount = state.getMinimumRaiseAmount();

        //raise amount after the call amount +

        // Very basic logic — replace with real strategy or LLM integration
        if (hand == null || hand.size() != 2) {
            action = "fold";
        } else if (isStrongHand(hand)) {
            action = "raise";
        } else if (callAmount == 0) {
            action = "check";
        } else {
            action = "call";
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("actionType", action);
        if ("raise".equals(action)) {
            payload.put("amount", raiseAmount + callAmount); // Raise amount is the minimum raise plus the call amount
        } else if ("call".equals(action)) {
            payload.put("amount", callAmount);
        } else {
//            payload.put("amount", 0); // For fold or check
        }

        log.info("[{}][{}]Decided action: {} with hand: {}, min raise {}, min call {}, payload [{}]", botName, id, action, hand, raiseAmount, callAmount, payload);


        return payload;
    }

    private static boolean isStrongHand(List<Card> hand) {
        // Basic example: pair or high cards
        String r1 = hand.get(0).getRank();
        String r2 = hand.get(1).getRank();
        return r1.equals(r2) || List.of("A", "K", "Q", "J").containsAll(List.of(r1, r2));
    }
}
