package com.yuyuan.wxmp;

import com.yuyuan.wxmp.model.entity.AiReplyRecord;
import com.yuyuan.wxmp.service.AiReplyRecordService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AI服务诊断测试
 * 用于测试AI服务的连接状态、响应时间、稳定性等
 */
@SpringBootTest
@Slf4j
public class AIServiceDiagnosisTest {

    @Autowired
    private OpenAiChatModel chatModel;

    @Autowired
    private AiReplyRecordService aiReplyRecordService;

    private static final String SYSTEM_PROMPT = "我想让你充当一个名为撷雯小筑微信公众号客服，回复内容控制在 200 字以内，在四秒内返回响应，并且回答的内容不要使用 markdown 格式，如果有链接可以使用 HTML 格式展示。";

    /**
     * 测试1：AI服务基础连接测试
     * 测试AI服务是否能够正常连接和响应
     */
    @Test
    public void testAIBasicConnection() {
        log.info("=== 开始AI服务基础连接测试 ===");
        
        try {
            long startTime = System.currentTimeMillis();
            
            ChatResponse chatResponse = chatModel.call(
                    new Prompt(
                            new SystemMessage(SYSTEM_PROMPT),
                            new UserMessage("你好，请问你是谁？")
                    ));
            
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            
            if (chatResponse != null && chatResponse.getResult() != null && chatResponse.getResult().getOutput() != null) {
                String replyContent = chatResponse.getResult().getOutput().getText();
                log.info("✅ AI服务连接成功");
                log.info("   响应时间: {} ms", responseTime);
                log.info("   响应内容: {}", replyContent);
                log.info("   测试结果: 通过");
            } else {
                log.error("❌ AI服务返回空响应");
                log.info("   响应时间: {} ms", responseTime);
                log.info("   测试结果: 失败");
            }
            
        } catch (Exception e) {
            log.error("❌ AI服务连接失败", e);
            log.info("   测试结果: 失败");
        }
        
        log.info("=== AI服务基础连接测试结束 ===\n");
    }

    /**
     * 测试2：AI服务响应时间测试
     * 测试AI服务在不同负载下的响应时间
     */
    @Test
    public void testAIResponseTime() {
        log.info("=== 开始AI服务响应时间测试 ===");
        
        List<String> testMessages = new ArrayList<>();
        testMessages.add("你好，请问你是谁？");
        testMessages.add("你们提供什么服务？");
        testMessages.add("如何联系你们？");
        testMessages.add("你们的营业时间是什么时候？");
        testMessages.add("你们的地址在哪里？");
        
        int testCount = testMessages.size();
        long totalResponseTime = 0;
        int successCount = 0;
        
        for (String message : testMessages) {
            try {
                long startTime = System.currentTimeMillis();
                
                ChatResponse chatResponse = chatModel.call(
                        new Prompt(
                                new SystemMessage(SYSTEM_PROMPT),
                                new UserMessage(message)
                        ));
                
                long endTime = System.currentTimeMillis();
                long responseTime = endTime - startTime;
                totalResponseTime += responseTime;
                
                if (chatResponse != null && chatResponse.getResult() != null && chatResponse.getResult().getOutput() != null) {
                    successCount++;
                    log.info("✅ 消息 '{}' 响应时间: {} ms", message, responseTime);
                } else {
                    log.error("❌ 消息 '{}' 返回空响应, 响应时间: {} ms", message, responseTime);
                }
                
                // 休息1秒，避免触发API频率限制
                Thread.sleep(1000);
                
            } catch (Exception e) {
                log.error("❌ 消息 '{}' 处理失败", message, e);
            }
        }
        
        double avgResponseTime = testCount > 0 ? (double) totalResponseTime / testCount : 0;
        log.info("=== AI服务响应时间测试结果 ===");
        log.info("测试总数: {}", testCount);
        log.info("成功数量: {}", successCount);
        log.info("失败数量: {}", testCount - successCount);
        log.info("平均响应时间: {:.2f} ms", avgResponseTime);
        log.info("成功率: {:.2f}%", (double) successCount / testCount * 100);
        log.info("=== AI服务响应时间测试结束 ===\n");
    }

    /**
     * 测试3：AI服务稳定性测试
     * 测试AI服务在并发请求下的稳定性
     */
    @Test
    public void testAIStability() {
        log.info("=== 开始AI服务稳定性测试 ===");
        
        int concurrentRequests = 3;
        int totalRequests = 5;
        
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentRequests);
        
        List<Long> responseTimes = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        
        for (int i = 0; i < totalRequests; i++) {
            final int requestId = i + 1;
            executorService.submit(() -> {
                try {
                    long startTime = System.currentTimeMillis();
                    
                    ChatResponse chatResponse = chatModel.call(
                            new Prompt(
                                    new SystemMessage(SYSTEM_PROMPT),
                                    new UserMessage("测试请求 " + requestId + ": 你好，请问今天天气怎么样？")
                            ));
                    
                    long endTime = System.currentTimeMillis();
                    long responseTime = endTime - startTime;
                    responseTimes.add(responseTime);
                    
                    if (chatResponse != null && chatResponse.getResult() != null && chatResponse.getResult().getOutput() != null) {
                        successCount.incrementAndGet();
                        log.info("✅ 请求 {} 成功, 响应时间: {} ms", requestId, responseTime);
                    } else {
                        failureCount.incrementAndGet();
                        log.error("❌ 请求 {} 返回空响应, 响应时间: {} ms", requestId, responseTime);
                    }
                    
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    log.error("❌ 请求 {} 处理失败", requestId, e);
                }
            });
            
            // 控制请求速率，避免触发API频率限制
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        executorService.shutdown();
        try {
            executorService.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long totalResponseTime = responseTimes.stream().mapToLong(Long::longValue).sum();
        double avgResponseTime = responseTimes.isEmpty() ? 0 : (double) totalResponseTime / responseTimes.size();
        
        log.info("=== AI服务稳定性测试结果 ===");
        log.info("并发数: {}", concurrentRequests);
        log.info("总请求数: {}", totalRequests);
        log.info("成功数量: {}", successCount.get());
        log.info("失败数量: {}", failureCount.get());
        log.info("平均响应时间: {:.2f} ms", avgResponseTime);
        log.info("成功率: {:.2f}%", (double) successCount.get() / totalRequests * 100);
        log.info("=== AI服务稳定性测试结束 ===\n");
    }

    /**
     * 测试4：AI服务完整调用流程测试
     * 测试从业务层调用AI服务的完整流程
     */
    @Test
    public void testAIFullProcess() {
        log.info("=== 开始AI服务完整调用流程测试 ===");
        
        try {
            String appId = "test-app-id";
            String fromUser = "test-user";
            String message = "你好，请问你能提供什么帮助？";
            
            AiReplyRecord aiReplyRecord = new AiReplyRecord();
            aiReplyRecord.setAppId(appId);
            aiReplyRecord.setFromUser(fromUser);
            aiReplyRecord.setMessage(message);
            
            long startTime = System.currentTimeMillis();
            String reply = aiReplyRecordService.aiReply(appId, fromUser, message, aiReplyRecord);
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            
            if (reply != null && !reply.isEmpty()) {
                log.info("✅ AI服务完整调用流程成功");
                log.info("   响应时间: {} ms", responseTime);
                log.info("   响应内容: {}", reply);
                log.info("   测试结果: 通过");
            } else {
                log.error("❌ AI服务完整调用流程失败，返回空响应");
                log.info("   响应时间: {} ms", responseTime);
                log.info("   测试结果: 失败");
            }
            
        } catch (Exception e) {
            log.error("❌ AI服务完整调用流程失败", e);
            log.info("   测试结果: 失败");
        }
        
        log.info("=== AI服务完整调用流程测试结束 ===\n");
    }

    /**
     * 测试5：AI服务异常处理测试
     * 测试AI服务在异常情况下的处理能力
     */
    @Test
    public void testAIExceptionHandling() {
        log.info("=== 开始AI服务异常处理测试 ===");
        
        try {
            // 使用超长消息测试
            String longMessage = "A".repeat(1000);
            
            AiReplyRecord aiReplyRecord = new AiReplyRecord();
            aiReplyRecord.setAppId("test-app-id");
            aiReplyRecord.setFromUser("test-user");
            aiReplyRecord.setMessage(longMessage);
            
            long startTime = System.currentTimeMillis();
            String reply = aiReplyRecordService.aiReply("test-app-id", "test-user", longMessage, aiReplyRecord);
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            
            if (reply != null) {
                log.info("✅ AI服务异常处理成功，返回了合理的错误提示");
                log.info("   响应时间: {} ms", responseTime);
                log.info("   响应内容: {}", reply);
                log.info("   测试结果: 通过");
            } else {
                log.error("❌ AI服务异常处理失败，返回了null");
                log.info("   响应时间: {} ms", responseTime);
                log.info("   测试结果: 失败");
            }
            
        } catch (Exception e) {
            log.error("❌ AI服务异常处理测试失败", e);
            log.info("   测试结果: 失败");
        }
        
        log.info("=== AI服务异常处理测试结束 ===\n");
    }
}
