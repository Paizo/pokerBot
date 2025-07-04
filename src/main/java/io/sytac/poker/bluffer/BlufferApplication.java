package io.sytac.poker.bluffer;

import io.sytac.poker.bluffer.client.PokerWebSocketClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlufferApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlufferApplication.class, args);
//		PokerWebSocketClient bot = new PokerWebSocketClient("ws://192.168.1.239:8080", "MyBot");
		PokerWebSocketClient bot = new PokerWebSocketClient("ws://localhost:8080", "MyBot");
		bot.connect();
	}

}
