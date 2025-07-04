package io.sytac.poker.bluffer.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class LLMClient {

//    private static final URI OPENAI_URL = URI.create("http://localhost:11434/v1/chat/completions");
    //LOCAL OLLAMA phi4:14b-q4_K_M
    private static final URI OPENAI_URL = URI.create("http://localhost:11434/api/generate");
//    private static final URI OPENAI_URL = URI.create("https://api.openai.com/v1/chat/completions");
    private final RestTemplate restTemplate = new RestTemplate();

    public String askOllama(String prompt) {
        System.out.println("Asking AI: " + prompt);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("");

//        Map<String, Object> message = new HashMap<>();
//        message.put("role", "user");
//        message.put("content", prompt);

        //FOR LOCAL OLLAMA
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "phi4:14b-q4_K_M");
        requestBody.put("prompt", prompt);
        requestBody.put("suffix", "");
        requestBody.put("stream", false);
        requestBody.put("temperature", 0.2);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_URL, request, Map.class);
            assert response.getBody() != null;
            log.info("Response: {}", response.getBody());
            return response.getBody().get("response").toString();
        } catch (Exception e) {
            System.out.println("Error Calling AI: " + e);
            e.printStackTrace();
            return null;
        }
    }

    public String callOpenAI(String promptText) {
        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(OPENAI_API_KEY); // Sets "Authorization: Bearer <API_KEY>"

        // Prepare the messages
        List<Map<String, String>> messages = List.of(
                Map.of("role", "user", "content", promptText)
        );

        // Request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o");
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.2);
        requestBody.put("stream", false);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_URL, request, Map.class);
            if (response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return message.get("content").toString();
                }
            }
        } catch (Exception e) {
            System.err.println("Error calling OpenAI: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
