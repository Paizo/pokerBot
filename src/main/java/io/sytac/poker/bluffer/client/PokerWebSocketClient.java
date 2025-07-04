package io.sytac.poker.bluffer.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sytac.poker.bluffer.service.BotLogic;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import io.sytac.poker.bluffer.model.*;

import java.net.URI;
import java.util.*;

public class PokerWebSocketClient extends WebSocketClient {
    private final String botName;
    private final ObjectMapper mapper = new ObjectMapper();

    private String playerId;
    private List<Card> myHand = new ArrayList<>();
    private GameState currentState;

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
                case "info" -> System.out.println("ℹ️ " + payload);
                case "error" -> System.err.println("❌ " + payload);
                case "player_hand" -> handlePrivateCards((List<Map<String, String>>) payload);
                case "state" -> handleGameState(mapper.convertValue(payload, GameState.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {

    }

    private void handlePrivateCards(List<Map<String, String>> payload) {
        myHand.clear();
        for (Map<String, String> cardData : payload) {
            myHand.add(new Card(cardData.get("rank"), cardData.get("suit")));
        }
        System.out.println("🃏 My Hand: " + myHand);
    }

    private void handleGameState(GameState state) {
        this.currentState = state;

        // Assign playerId
        if (playerId == null) {
            currentState.getPlayers().stream()
                    .filter(p -> botName.equals(p.getName()))
                    .findFirst()
                    .ifPresent(p -> playerId = p.getId());
        }

        // Check if it's our turn
        if (playerId != null && playerId.equals(state.getCurrentPlayerId())) {
            Player me = state.getPlayers().stream()
                    .filter(p -> p.getId().equals(playerId))
                    .findFirst().orElse(null);

            if (me != null && "active".equals(me.getStatus())) {
                Map<String, Object> actionPayload = BotLogic.decideAction(state, myHand);
                sendJson("action", actionPayload);
            }
        }
    }

    private void sendJson(String type, Object payload) {
        try {
            Map<String, Object> message = Map.of("type", type, "payload", payload);
            send(mapper.writeValueAsString(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

