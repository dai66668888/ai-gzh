package com.yuyuan.wxmp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI服务诊断工具
 * 用于诊断AI服务的连接状态、响应时间、认证授权等
 */
@RestController
@RequestMapping("/ai-diagnosis")
@Slf4j
public class AIDiagnosis {

    @Autowired
    private OpenAiChatModel chatModel;

    private static final String SYSTEM_PROMPT = "我想让你充当一个名为撷雯小筑微信公众号客服，回复内容控制在 200 字以内，在四秒内返回响应，并且回答的内容不要使用 markdown 格式，如果有链接可以使用 HTML 格式展示。";

    /**
     * 执行AI服务诊断
     */
    @GetMapping("/run")
    public Map<String, Object> runDiagnosis() {
        log.info("=== 开始AI服务诊断 ===");
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> tests = new ArrayList<>();
        
        // 1. 网络连接测试
        Map<String, Object> networkTest = testNetworkConnection();
        tests.add(networkTest);
        
        // 2. API调用响应时间测试
        Map<String, Object> responseTimeTest = testApiResponseTime();
        tests.add(responseTimeTest);
        
        // 3. 认证授权状态测试
        Map<String, Object> authTest = testAuthStatus();
        tests.add(authTest);
        
        // 4. 数据传输完整性测试
        Map<String, Object> dataIntegrityTest = testDataIntegrity();
        tests.add(dataIntegrityTest);
        
        // 统计结果
        long successCount = tests.stream().filter(test -> "success".equals(test.get("status"))).count();
        long failedCount = tests.size() - successCount;
        
        result.put("totalTests", tests.size());
        result.put("successCount", successCount);
        result.put("failedCount", failedCount);
        result.put("tests", tests);
        result.put("overallStatus", successCount == tests.size() ? "success" : "warning");
        
        log.info("=== AI服务诊断完成 ===");
        
        return result;
    }

    /**
     * 测试1：网络连接测试
     * 测试AI服务是否能够正常连接
     */
    private Map<String, Object> testNetworkConnection() {
        Map<String, Object> result = new HashMap<>();
        result.put("testName", "网络连接测试");
        
        try {
            log.info("正在测试网络连接...");
            long startTime = System.currentTimeMillis();
            
            // 尝试调用AI服务
            ChatResponse chatResponse = chatModel.call(
                    new Prompt(
                            new SystemMessage(SYSTEM_PROMPT),
                            new UserMessage("测试连接")
                    ));
            
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            
            if (chatResponse != null) {
                log.info("✅ 网络连接成功");
                log.info("   响应时间: {} ms", responseTime);
                
                result.put("status", "success");
                result.put("message", "网络连接成功");
                result.put("responseTime", responseTime + " ms");
            } else {
                log.error("❌ 网络连接失败：返回空响应");
                
                result.put("status", "error");
                result.put("message", "网络连接失败：返回空响应");
            }
            
        } catch (Exception e) {
            log.error("❌ 网络连接失败：{}", e.getMessage());
            log.debug("详细错误信息", e);
            
            result.put("status", "error");
            result.put("message", "网络连接失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 测试2：API调用响应时间测试
     * 测试AI服务的响应时间
     */
    private Map<String, Object> testApiResponseTime() {
        Map<String, Object> result = new HashMap<>();
        result.put("testName", "API调用响应时间测试");
        
        try {
            log.info("正在测试API响应时间...");
            long totalTime = 0;
            int testCount = 3;
            List<Long> responseTimes = new ArrayList<>();
            
            for (int i = 0; i < testCount; i++) {
                long startTime = System.currentTimeMillis();
                
                ChatResponse chatResponse = chatModel.call(
                        new Prompt(
                                new SystemMessage(SYSTEM_PROMPT),
                                new UserMessage("测试响应时间 " + (i + 1))
                        ));
                
                long endTime = System.currentTimeMillis();
                long responseTime = endTime - startTime;
                totalTime += responseTime;
                responseTimes.add(responseTime);
                
                log.info("   测试 {}: {} ms", i + 1, responseTime);
                
                // 休息1秒，避免触发API频率限制
                Thread.sleep(1000);
            }
            
            double avgTime = (double) totalTime / testCount;
            log.info("✅ API响应时间测试完成");
            log.info("   平均响应时间: {:.2f} ms", avgTime);
            
            String performanceLevel;
            if (avgTime < 2000) {
                performanceLevel = "优秀";
                log.info("   响应时间优秀");
            } else if (avgTime < 5000) {
                performanceLevel = "良好";
                log.info("   响应时间良好");
            } else {
                performanceLevel = "一般";
                log.warn("   响应时间过长，建议优化");
            }
            
            result.put("status", "success");
            result.put("message", "API响应时间测试完成");
            result.put("testCount", testCount);
            result.put("avgResponseTime", String.format("%.2f ms", avgTime));
            result.put("performanceLevel", performanceLevel);
            result.put("detailedResults", responseTimes);
            
        } catch (Exception e) {
            log.error("❌ API响应时间测试失败：{}", e.getMessage());
            log.debug("详细错误信息", e);
            
            result.put("status", "error");
            result.put("message", "API响应时间测试失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 测试3：认证授权状态测试
     * 测试AI服务的认证授权是否正常
     */
    private Map<String, Object> testAuthStatus() {
        Map<String, Object> result = new HashMap<>();
        result.put("testName", "认证授权状态测试");
        
        try {
            log.info("正在测试认证授权状态...");
            
            ChatResponse chatResponse = chatModel.call(
                    new Prompt(
                            new SystemMessage(SYSTEM_PROMPT),
                            new UserMessage("测试认证授权")
                    ));
            
            if (chatResponse != null) {
                log.info("✅ 认证授权状态正常");
                log.info("   能够成功调用AI服务");
                
                result.put("status", "success");
                result.put("message", "认证授权状态正常");
                result.put("details", "能够成功调用AI服务");
            } else {
                log.error("❌ 认证授权状态异常：返回空响应");
                
                result.put("status", "error");
                result.put("message", "认证授权状态异常：返回空响应");
                result.put("details", "AI服务返回空响应，可能是认证授权问题");
            }
            
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            log.error("❌ 认证授权状态测试失败：{}", errorMessage);
            
            String errorType = "其他错误";
            if (errorMessage.contains("401") || errorMessage.contains("unauthorized") || errorMessage.contains("token")) {
                errorType = "认证失败，可能是API密钥无效或已过期";
                log.error("   错误类型：认证失败，可能是API密钥无效或已过期");
            } else if (errorMessage.contains("403") || errorMessage.contains("forbidden")) {
                errorType = "权限不足，当前API密钥没有调用该服务的权限";
                log.error("   错误类型：权限不足，当前API密钥没有调用该服务的权限");
            } else if (errorMessage.contains("429") || errorMessage.contains("RPM限制") || errorMessage.contains("rate limit")) {
                errorType = "API调用频率限制";
                log.error("   错误类型：API调用频率限制");
            } else {
                log.error("   错误类型：其他错误");
            }
            
            log.debug("详细错误信息", e);
            
            result.put("status", "error");
            result.put("message", "认证授权状态测试失败：" + errorMessage);
            result.put("details", "错误类型：" + errorType);
        }
        
        return result;
    }

    /**
     * 测试4：数据传输完整性测试
     * 测试AI服务返回的数据是否完整
     */
    private Map<String, Object> testDataIntegrity() {
        Map<String, Object> result = new HashMap<>();
        result.put("testName", "数据传输完整性测试");
        
        try {
            log.info("正在测试数据传输完整性...");
            
            String testMessage = "请返回'测试数据完整性'这几个字，不要添加任何其他内容";
            
            ChatResponse chatResponse = chatModel.call(
                    new Prompt(
                            new SystemMessage(SYSTEM_PROMPT),
                            new UserMessage(testMessage)
                    ));
            
            if (chatResponse != null && chatResponse.getResult() != null && chatResponse.getResult().getOutput() != null) {
                String reply = chatResponse.getResult().getOutput().getText();
                log.info("✅ 数据传输完整性测试完成");
                log.info("   请求内容: {}", testMessage);
                log.info("   响应内容: {}", reply);
                
                if (reply.contains("测试数据完整性")) {
                    log.info("   响应内容符合预期，数据传输完整");
                    
                    result.put("status", "success");
                    result.put("message", "数据传输完整性测试完成");
                    result.put("details", "响应内容符合预期，数据传输完整");
                    result.put("request", testMessage);
                    result.put("response", reply);
                } else {
                    log.warn("   响应内容不完全符合预期：{}", reply);
                    
                    result.put("status", "warning");
                    result.put("message", "数据传输完整性测试完成，但响应内容不完全符合预期");
                    result.put("details", "响应内容不完全符合预期，可能是AI模型理解偏差");
                    result.put("request", testMessage);
                    result.put("response", reply);
                }
                
            } else {
                log.error("❌ 数据传输完整性测试失败：返回数据不完整");
                
                result.put("status", "error");
                result.put("message", "数据传输完整性测试失败：返回数据不完整");
                result.put("details", "AI服务返回的数据不完整");
            }
            
        } catch (Exception e) {
            log.error("❌ 数据传输完整性测试失败：{}", e.getMessage());
            log.debug("详细错误信息", e);
            
            result.put("status", "error");
            result.put("message", "数据传输完整性测试失败：" + e.getMessage());
            result.put("details", "调用AI服务时发生异常");
        }
        
        return result;
    }
}
