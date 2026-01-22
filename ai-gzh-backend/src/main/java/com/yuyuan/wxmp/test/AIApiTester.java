package com.yuyuan.wxmp.test;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

public class AIApiTester {
    
    private static final String BASE_URL = "https://api.mindcraft.com.cn";
    private static final String API_KEY = "MC-82795BA52DD44FF5ADCE1A8C9F97638C";
    private static final String MODEL = "deepseek-r1-free";
    
    public static void main(String[] args) {
        System.out.println("=== AI API Connectivity Test ===");
        
        // 1. Basic connectivity test
        System.out.println("\n1. Basic Connectivity Test");
        testBasicConnectivity();
        
        // 2. API endpoint test
        System.out.println("\n2. API Endpoint Test");
        testApiEndpoints();
        
        // 3. Authentication test
        System.out.println("\n3. Authentication Test");
        testAuthentication();
        
        // 4. Model availability test
        System.out.println("\n4. Model Availability Test");
        testModelAvailability();
        
        // 5. Complete AI call test
        System.out.println("\n5. Complete AI Call Test");
        testCompleteAiCall();
        
        System.out.println("\n=== Test Complete ===");
    }
    
    private static void testBasicConnectivity() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            // Test root path
            ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL, String.class);
            System.out.println("   Root path connection: " + response.getStatusCode());
            
            // Test /v1 path
            response = restTemplate.getForEntity(BASE_URL + "/v1", String.class);
            System.out.println("   /v1 path connection: " + response.getStatusCode());
            
        } catch (Exception e) {
            System.out.println("   Connection failed: " + e.getMessage());
        }
    }
    
    private static void testApiEndpoints() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            // Test different endpoints
            String[] endpoints = {
                "/v1/chat/completions",
                "/v1/models",
                "/v1/completions"
            };
            
            for (String endpoint : endpoints) {
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Authorization", "Bearer " + API_KEY);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    
                    HttpEntity<String> entity = new HttpEntity<>(headers);
                    ResponseEntity<String> response = restTemplate.exchange(
                        BASE_URL + endpoint, 
                        HttpMethod.GET, 
                        entity, 
                        String.class
                    );
                    
                    System.out.println("   " + endpoint + ": " + response.getStatusCode());
                    
                } catch (Exception e) {
                    System.out.println("   " + endpoint + ": Failed - " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.out.println("   API endpoint test failed: " + e.getMessage());
        }
    }
    
    private static void testAuthentication() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            
            // Create test request
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL);
            requestBody.put("messages", Arrays.asList(
                Map.of("role", "user", "content", "Test connection")
            ));
            requestBody.put("max_tokens", 50);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + API_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
            
            System.out.println("   Request URL: " + BASE_URL + "/v1/chat/completions");
            System.out.println("   Request Headers: Authorization=Bearer [API_KEY]");
            System.out.println("   Request Body: " + objectMapper.writeValueAsString(requestBody));
            
            ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/v1/chat/completions",
                HttpMethod.POST,
                entity,
                String.class
            );
            
            System.out.println("   Authentication test result: " + response.getStatusCode());
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("   Response content: " + response.getBody());
            }
            
        } catch (Exception e) {
            System.out.println("   Authentication test failed: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
    
    private static void testModelAvailability() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + API_KEY);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Get available models list
            ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/v1/models",
                HttpMethod.GET,
                entity,
                String.class
            );
            
            System.out.println("   Models list retrieval: " + response.getStatusCode());
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("   Available models: " + response.getBody());
                
                // Check if target model is in the list
                if (response.getBody().contains(MODEL)) {
                    System.out.println("   ✓ Target model " + MODEL + " is available");
                } else {
                    System.out.println("   ✗ Target model " + MODEL + " is not available");
                }
            }
            
        } catch (Exception e) {
            System.out.println("   Model availability test failed: " + e.getMessage());
        }
    }
    
    private static void testCompleteAiCall() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            
            long startTime = System.currentTimeMillis();
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL);
            requestBody.put("messages", Arrays.asList(
                Map.of("role", "system", "content", "You are a helpful assistant"),
                Map.of("role", "user", "content", "Hello, please introduce yourself")
            ));
            requestBody.put("max_tokens", 100);
            requestBody.put("temperature", 0.7);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + API_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
            
            System.out.println("   Sending complete AI request...");
            ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/v1/chat/completions",
                HttpMethod.POST,
                entity,
                String.class
            );
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            System.out.println("   Complete AI call result: " + response.getStatusCode());
            System.out.println("   Response time: " + duration + "ms");
            
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("   ✓ AI call successful");
                System.out.println("   Response content: " + response.getBody());
            } else {
                System.out.println("   ✗ AI call failed");
                System.out.println("   Error response: " + response.getBody());
            }
            
        } catch (Exception e) {
            System.out.println("   Complete AI call failed: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}