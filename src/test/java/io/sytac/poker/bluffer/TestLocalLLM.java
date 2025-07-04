package io.sytac.poker.bluffer;

import io.sytac.poker.bluffer.client.LLMClient;
import org.junit.jupiter.api.Test;

public class TestLocalLLM {


    @Test
    public void asd () {
        LLMClient llmClient = new LLMClient();
        System.out.println(llmClient.askOllama("say something"));
    }
}
