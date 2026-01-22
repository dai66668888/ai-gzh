package com.yuyuan.wxmp.test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 微信签名生成器
 * 用于生成正确的微信请求签名
 */
public class WeChatSignatureGenerator {

    // 从数据库获取的token
    private static final String TOKEN = "daishuwang";

    /**
     * 生成微信签名
     * @param timestamp 时间戳
     * @param nonce 随机字符串
     * @param token 令牌
     * @return 生成的签名
     */
    public static String generateSignature(String timestamp, String nonce, String token) {
        // 1. 将token、timestamp、nonce三个参数进行字典序排序
        String[] arr = new String[]{token, timestamp, nonce};
        Arrays.sort(arr);

        // 2. 将三个参数字符串拼接成一个字符串进行sha1加密
        StringBuilder content = new StringBuilder();
        for (String s : arr) {
            content.append(s);
        }

        // 3. 进行sha1加密
        return sha1(content.toString());
    }

    /**
     * SHA1加密
     * @param str 需要加密的字符串
     * @return 加密后的字符串
     */
    private static String sha1(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(str.getBytes());
            byte[] messageDigest = digest.digest();

            // 创建十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String shaHex = Integer.toHexString(b & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1加密失败", e);
        }
    }

    /**
     * 验证签名是否正确
     * @param signature 微信传来的签名
     * @param timestamp 时间戳
     * @param nonce 随机字符串
     * @param token 令牌
     * @return 验证结果
     */
    public static boolean checkSignature(String signature, String timestamp, String nonce, String token) {
        String generatedSignature = generateSignature(timestamp, nonce, token);
        return generatedSignature != null && generatedSignature.equals(signature);
    }

    /**
     * 生成测试用的完整URL参数
     * @param timestamp 时间戳
     * @param nonce 随机字符串
     * @return 包含正确签名的参数字符串
     */
    public static String generateUrlParams(String timestamp, String nonce) {
        String signature = generateSignature(timestamp, nonce, TOKEN);
        return String.format("signature=%s&timestamp=%s&nonce=%s", signature, timestamp, nonce);
    }

    /**
     * 主方法：生成测试用的签名参数
     */
    public static void main(String[] args) {
        // 生成当前时间戳
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        // 生成随机字符串
        String nonce = "test" + (int)(Math.random() * 10000);

        System.out.println("=== 微信签名生成器 ===");
        System.out.println("时间戳: " + timestamp);
        System.out.println("随机字符串: " + nonce);
        System.out.println("Token: " + TOKEN);
        
        String signature = generateSignature(timestamp, nonce, TOKEN);
        System.out.println("生成的签名: " + signature);
        
        String urlParams = generateUrlParams(timestamp, nonce);
        System.out.println("URL参数: " + urlParams);
        
        // 验证签名
        boolean isValid = checkSignature(signature, timestamp, nonce, TOKEN);
        System.out.println("签名验证: " + (isValid ? "✅ 正确" : "❌ 错误"));
        
        System.out.println("\n=== 测试URL ===");
        String testUrl = "http://localhost:8102/api/wx/msg/wx4dfe426f30714a55?" + urlParams + "&openid=test_user";
        System.out.println(testUrl);
    }
}