package com.yuyuan.wxmp.test;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class WeChatMessageSimulator {
    
    private static final String SERVER_URL = "http://localhost:8102/api/wx/msg/wx4dfe426f30714a55";
    private static final String TOKEN = "daishuwang";
    
    public static void main(String[] args) {
        System.out.println("=== 微信公众号消息模拟器 ===");
        
        // 测试不同的消息
        String[] testMessages = {
            "你好",
            "请问有什么功能",
            "测试AI回复",
            "你是谁",
            "帮助"
        };
        
        for (String message : testMessages) {
            System.out.println("\n--- 测试消息: " + message + " ---");
            testWeChatMessage(message);
            
            // 等待2秒再发送下一条消息
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("\n=== 测试完成 ===");
    }
    
    private static void testWeChatMessage(String content) {
        try {
            // 生成模拟的微信XML消息
            String xmlMessage = generateWeChatXmlMessage(content);
            
            System.out.println("发送消息到: " + SERVER_URL);
            System.out.println("消息内容: " + xmlMessage);
            
            // 发送POST请求
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/xml; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/xml");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(15000); // 15秒超时，等待AI回复
            
            // 发送消息体
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = xmlMessage.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // 获取响应
            int responseCode = connection.getResponseCode();
            System.out.println("响应码: " + responseCode);
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取成功响应
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    System.out.println("AI回复: " + extractContentFromXml(response.toString()));
                }
            } else {
                // 读取错误响应
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    System.out.println("错误响应: " + errorResponse.toString());
                }
            }
            
            connection.disconnect();
            
        } catch (Exception e) {
            System.out.println("发送消息失败: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
    
    private static String generateWeChatXmlMessage(String content) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String msgId = String.valueOf(System.currentTimeMillis());
        
        return "<xml>" +
               "<ToUserName><![CDATA[gh_c0c0c049878c]]></ToUserName>" +
               "<FromUserName><![CDATA[oT7233Gv9K2W5Ja-eH7MHWOfCJbE]]></FromUserName>" +
               "<CreateTime>" + timestamp + "</CreateTime>" +
               "<MsgType><![CDATA[text]]></MsgType>" +
               "<Content><![CDATA[" + content + "]]></Content>" +
               "<MsgId>" + msgId + "</MsgId>" +
               "</xml>";
    }
    
    private static String extractContentFromXml(String xml) {
        // 简单的XML解析，提取Content内容
        int start = xml.indexOf("<Content><![CDATA[");
        if (start != -1) {
            start += "<Content><![CDATA[".length();
            int end = xml.indexOf("]]></Content>", start);
            if (end != -1) {
                return xml.substring(start, end);
            }
        }
        return "无法解析回复内容";
    }
}