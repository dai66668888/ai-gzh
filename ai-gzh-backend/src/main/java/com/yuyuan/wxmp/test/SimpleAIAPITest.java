package com.yuyuan.wxmp.test;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class SimpleAIAPITest {
    
    private static final String BASE_URL = "https://api.mindcraft.com.cn";
    private static final String API_KEY = "MC-82795BA52DD44FF5ADCE1A8C9F97638C";
    private static final String MODEL = "deepseek-r1-free";
    
    public static void main(String[] args) {
        System.out.println("=== Simple AI API Test ===");
        
        // 1. Test basic connectivity
        System.out.println("\n1. Testing Basic Connectivity");
        testBasicConnectivity();
        
        // 2. Test AI API call
        System.out.println("\n2. Testing AI API Call");
        testAIAPICall();
        
        System.out.println("\n=== Test Complete ===");
    }
    
    private static void testBasicConnectivity() {
        try {
            URL url = new URL(BASE_URL + "/v1");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            System.out.println("   Base URL connectivity: " + responseCode + " " + connection.getResponseMessage());
            
            // Test /v1/chat/completions endpoint
            url = new URL(BASE_URL + "/v1/chat/completions");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            responseCode = connection.getResponseCode();
            System.out.println("   Chat completions endpoint: " + responseCode + " " + connection.getResponseMessage());
            
            connection.disconnect();
            
        } catch (Exception e) {
            System.out.println("   Connectivity test failed: " + e.getMessage());
        }
    }
    
    private static void testAIAPICall() {
        try {
            URL url = new URL(BASE_URL + "/v1/chat/completions");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            // Create JSON request body
            String jsonBody = "{"
                + "\"model\":\"" + MODEL + "\","
                + "\"messages\":["
                + "{\"role\":\"user\",\"content\":\"Hello, test connection\"}"
                + "],"
                + "\"max_tokens\":50,"
                + "\"temperature\":0.7"
                + "}";
            
            System.out.println("   Request URL: " + BASE_URL + "/v1/chat/completions");
            System.out.println("   Request Body: " + jsonBody);
            
            // Set up the connection
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            
            // Send the request
            long startTime = System.currentTimeMillis();
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // Get the response
            int responseCode = connection.getResponseCode();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            System.out.println("   Response Code: " + responseCode);
            System.out.println("   Response Time: " + duration + "ms");
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read successful response
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    System.out.println("   ✓ AI API call successful");
                    System.out.println("   Response: " + response.toString());
                }
            } else {
                // Read error response
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    System.out.println("   ✗ AI API call failed");
                    System.out.println("   Error Response: " + errorResponse.toString());
                }
            }
            
            connection.disconnect();
            
        } catch (Exception e) {
            System.out.println("   AI API call failed: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}