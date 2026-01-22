package com.yuyuan.wxmp.test;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class WeChatMessageSimulatorFixed {
    
    private static final String SERVER_URL = "http://localhost:8102/api/wx/msg/wx4dfe426f30714a55";
    private static final String TOKEN = "daishuwang";
    
    public static void main(String[] args) {
        System.out.println("=== 微信公众号消息模拟器 (修正版) ===");
        
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
            
            // 等待3秒再发送下一条消息
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("\n=== 测试完成 ===");
    }
    
    private static void testWeChatMessage(String content) {
        try {
            // 生成必要参数
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String nonce = generateNonce();
            String openId = "oT7233Gv9K2W5Ja-eH7MHWOfCJbE";
            
            // 生成微信消息体
            String requestBody = generateWeChatXmlMessage(content);
            
            // 生成签名
            String signature = generateSignature(TOKEN, timestamp, nonce);
            
            // 构建完整的URL
            String fullUrl = SERVER_URL + "?signature=" + signature + 
                           "&timestamp=" + timestamp + 
                           "&nonce=" + nonce + 
                           "&openid=" + openId;
            
            System.out.println("发送消息到: " + fullUrl);
            System.out.println("消息内容: " + requestBody);
            
            // 发送POST请求
            URL url = new URL(fullUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/xml; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/xml");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(15000); // 15秒超时，等待AI回复
            
            // 发送消息体
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
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
                    System.out.println("原始响应: " + response.toString());
                    String extractedContent = extractContentFromXml(response.toString());
                    System.out.println("AI回复: " + extractedContent);
                    
                    // 判断是否是默认回复
                    if (extractedContent.contains("正在思考中，请 10 秒后再次发送")) {
                        System.out.println("⚠️ 收到默认回复，AI调用可能失败");
                    } else {
                        System.out.println("✅ 收到AI回复");
                    }
                }
            } else {
                // 读取错误响应
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    System.out.println("❌ 错误响应: " + errorResponse.toString());
                }
            }
            
            connection.disconnect();
            
        } catch (Exception e) {
            System.out.println("❌ 发送消息失败: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String generateNonce() {
        return String.valueOf((int)(Math.random() * 100000000));
    }
    
    private static String generateSignature(String token, String timestamp, String nonce) {
        try {
            // 微信签名算法：将token、timestamp、nonce按字典序排序后拼接，然后SHA1加密
            String[] arr = {token, timestamp, nonce};
            Arrays.sort(arr);
            StringBuilder content = new StringBuilder();
            for (String str : arr) {
                content.append(str);
            }
            
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(content.toString().getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String shaHex = Integer.toHexString(b & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1算法不可用", e);
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