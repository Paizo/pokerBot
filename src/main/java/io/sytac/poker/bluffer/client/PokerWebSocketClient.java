package io.sytac.poker.bluffer.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sytac.poker.bluffer.service.BotLogic;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import io.sytac.poker.bluffer.model.*;

import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

@Slf4j
public class PokerWebSocketClient extends WebSocketClient {
    private final String botName;
    private final ObjectMapper mapper = new ObjectMapper();

    private String playerId;
    private List<Card> myHand = new ArrayList<>();
    private GameState currentState;
    Pattern pattern = Pattern.compile("Your ID: ([a-f0-9\\-]+)\\. You are a player");

    public PokerWebSocketClient(String serverUrl, String botName) {
        super(URI.create(serverUrl));
        this.botName = botName;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("✅ Connected to server");
        sendJson("join", Map.of("name", botName));
    }

    @Override
    public void onMessage(String message) {
        try {
            Map<String, Object> root = mapper.readValue(message, new TypeReference<>() {});
            String type = (String) root.get("type");
            Object payload = root.get("payload");

            switch (type) {
                case "info" -> handleInfo((String) payload);
                case "error" -> log.error("[{}][{}] ❌ {}", botName, playerId, payload);
                case "player_hand" -> handlePrivateCards((List<Map<String, String>>) payload);
                case "state" -> handleGameState(mapper.convertValue(payload, GameState.class));
            }
        } catch (Exception e) {
            log.error("[{}][{}] onMessageError {}", botName, playerId, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        log.info("[{}][{}] onClose()", botName, playerId);
    }

    @Override
    public void onError(Exception e) {
        log.error("[{}][{}] {}", botName, playerId, e.getMessage());
    }

    private void handleInfo(String message) {
        log.info("[{}][{}] ℹ️: {}", botName, playerId, message);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            playerId = matcher.group(1);
            log.info("[{}] Player ID set to: {}", botName, playerId);
        } else {
            log.error("[{}][{}]Failed to extract player ID from info message.", botName, playerId);
        }
    }

    private void handlePrivateCards(List<Map<String, String>> payload) {
        myHand.clear();
        for (Map<String, String> cardData : payload) {
            myHand.add(new Card(cardData.get("rank"), cardData.get("suit")));
        }
        log.info("[{}][{}]🃏 My Hand: {}", botName, playerId, myHand);
    }

    private void handleGameState(GameState state) {
        if (playerId == null) {
            log.error("[{}] Player ID is null", botName);
            return;
        }

        if (state == null) {
            log.error("[{}][{}] Game state is null", botName, playerId);
            return;
        }

        if (!playerId.equals(state.getCurrentPlayerId())) {
            log.info("[{}][{}]⏳ Waiting for my turn...[turn of: {}]", botName, playerId, state.getCurrentPlayerId());
            return;
        }

        Map<String, Object> actionPayload = BotLogic.decideAction(botName, playerId, state, myHand);
        sendJson("action", actionPayload);
    }

    private void sendJson(String type, Object payload) {
        try {
            Map<String, Object> message = Map.of("type", type, "payload", payload);
            send(mapper.writeValueAsString(message));
        } catch (Exception e) {
            log.error("[{}][{}] {}", botName, playerId, e.getMessage());
            e.printStackTrace();
        }
    }
}

