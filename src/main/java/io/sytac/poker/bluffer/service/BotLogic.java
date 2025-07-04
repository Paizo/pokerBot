package io.sytac.poker.bluffer.service;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sytac.poker.bluffer.client.LLMClient;
import io.sytac.poker.bluffer.model.Card;
import io.sytac.poker.bluffer.model.GameState;
import io.sytac.poker.bluffer.model.Player;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BotLogic {
    private static LLMClient llmClient = new LLMClient();
    private static ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public static String decideAction(String botName, String id, GameState state, List<Card> hand) {

        String response = llmClient.askOllama(
"You are an expert Texas Hold'em poker AI. Based on the input JSON below, choose the best next action and respond only with the exact JSON action object as shown.\n" +
        "Do not include any explanation, commentary, or additional text — only return the JSON.\n" +
        "Input:\n" +
        objectMapper.writeValueAsString(state) + "\n" +
        "Actions you can choose from (use this exact JSON format):\n" +
        "Fold:\n" +
        "{ \"type\": \"action\", \"payload\": { \"actionType\": \"fold\" } }\n" +
        "Call:\n" +
        "{ \"type\": \"action\", \"payload\": { \"actionType\": \"call\" } }\n" +
        "Raise (specify amount):\n" +
        "{ \"type\": \"action\", \"payload\": { \"actionType\": \"raise\", \"amount\": 100 } }\n" +
        "Check (only if allowed):\n" +
        "{ \"type\": \"action\", \"payload\": { \"actionType\": \"check\" } }\n" +
        "Evaluate your decision based on:\n" +
        "Strength of the player's hand (hole cards + community cards)\n" +
        "Pot odds and bet size\n" +
        "Player's available chips\n" +
        "Risk vs reward\n" +
        "Be mindful of the game state, minimum amount for raise is minimum raise amount + minimum call amount.\n" +
        "yes also the game state may contains error expecially on minumum call and raise, so consider if the previous player for example is big blind then you need to call with the big blind amount.\n" +
        "Return only one JSON object from above — nothing else."
        );

        log.info("[{}][{}]AI response: {}", botName, id, response);
        response = cleanupResponse(response);
//        Map responseMap = objectMapper.readValue(response, Map.class);

//        log.info("[{}][{}]Decided action: {}, payload [{}], state[{}]", botName, id, responseMap.get("action"), responseMap, state);

        return response;
//        return responseMap;
    }

    private static String cleanupResponse(String response) {
        return response
                .replace("```json", "")
                .replace("```", "");
    }


    public static Map<String, Object> decideActionOld(String botName, String id, GameState state, List<Card> hand) {
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
