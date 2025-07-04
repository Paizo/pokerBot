package io.sytac.poker.bluffer;

import io.sytac.poker.bluffer.client.PokerWebSocketClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class MultiBotIntegrationTest {

    private static final List<PokerWebSocketClient> bots = new ArrayList<>();

    @Test
    public void testMultipleBotsJoiningAndInteracting() throws Exception {
        int numberOfBots = 3;

        for (int i = 1; i <= numberOfBots; i++) {
            String botName = "Bot" + i;
            URI uri = new URI("ws://localhost:8080");
            PokerWebSocketClient bot = new PokerWebSocketClient(uri.toString(), botName);

            bot.connectBlocking(5, TimeUnit.SECONDS); // Wait for connection
            assertTrue(bot.isOpen(), botName + " failed to connect.");

//            bot.sendJoinMessage();
            bots.add(bot);
        }

        // Wait and observe for some messages (simulate game turns etc.)
        Thread.sleep(100000);

        for (PokerWebSocketClient bot : bots) {
            assertTrue(bot.isOpen(), "Bot disconnected unexpectedly.");
        }
    }

    @AfterAll
    public static void cleanUp() {
        bots.forEach(bot -> {
            try {
                bot.closeBlocking();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}

