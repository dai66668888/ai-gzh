package com.yuyuan.wxmp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuyuan.wxmp.common.ErrorCode;
import com.yuyuan.wxmp.exception.BusinessException;
import com.yuyuan.wxmp.mapper.AiReplyRecordMapper;
import com.yuyuan.wxmp.model.entity.AiReplyRecord;
import com.yuyuan.wxmp.model.enums.WxAiReplyStatusEnum;
import com.yuyuan.wxmp.service.AiReplyRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author cq
 * @description 针对表【ai_reply_record(AI 回复内容记录)】的数据库操作Service实现
 * @createDate 2025-03-14 19:16:00
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiReplyRecordServiceImpl extends ServiceImpl<AiReplyRecordMapper, AiReplyRecord>
        implements AiReplyRecordService {

    private final OpenAiChatModel chatModel;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String SYSTEM_PROMPT = "我想让你充当一个名为撷雯小筑微信公众号客服，回复内容控制在 200 字以内，在四秒内返回响应，并且回答的内容不要使用 markdown 格式，如果有链接可以使用 HTML 格式展示。";
    private static final String RATE_LIMIT_KEY_PREFIX = "ai_reply_rate_limit:";
    private static final String CACHE_KEY_PREFIX = "ai_reply_cache:";
    private static final int RATE_LIMIT_MAX = 2; // 每分钟最大请求次数
    private static final long RATE_LIMIT_EXPIRE = 60; // 限流时间窗口，单位：秒
    private static final long CACHE_EXPIRE = 1800; // 缓存过期时间，单位：秒（30分钟）

    @Override
    public String aiReply(String appId, String fromUser, String message, AiReplyRecord aiReplyRecord) {
        try {
            // 0. 增强输入消息检查
            if (StringUtils.isBlank(message)) {
                log.info("收到空消息 - appId: {}, fromUser: {}", appId, fromUser);
                return "请输入有效的消息内容，我将为您提供帮助";
            }
            
            // 处理特殊字符消息
            String trimmedMessage = message.trim();
            if (trimmedMessage.equals("？") || trimmedMessage.equals("?") || trimmedMessage.equals("！") || trimmedMessage.equals("!") || trimmedMessage.equals("。") || trimmedMessage.equals(".")) {
                log.info("收到无效特殊字符消息 - appId: {}, fromUser: {}, message: {}", appId, fromUser, message);
                return "请输入具体的问题或需求，我将为您提供帮助";
            }
            
            // 检查消息长度
            if (message.length() > 500) {
                log.info("收到过长消息 - appId: {}, fromUser: {}, messageLength: {}", appId, fromUser, message.length());
                return "消息内容过长，请控制在500字以内，感谢您的理解";
            }
            
            // 1. 实现请求缓存机制
            String cacheKey = CACHE_KEY_PREFIX + appId + ":" + fromUser + ":" + message.hashCode();
            String cachedReply = redisTemplate.opsForValue().get(cacheKey);
            if (cachedReply != null) {
                log.info("返回缓存的AI回复 - appId: {}, fromUser: {}, message: {}", appId, fromUser, message);
                aiReplyRecord.setReplyMessage(cachedReply);
                aiReplyRecord.setReplyStatus(WxAiReplyStatusEnum.REPLIED.getValue());
                this.lambdaUpdate()
                        .eq(AiReplyRecord::getId, aiReplyRecord.getId())
                        .set(AiReplyRecord::getReplyMessage, cachedReply)
                        .set(AiReplyRecord::getReplyStatus, WxAiReplyStatusEnum.REPLIED.getValue())
                        .update();
                return cachedReply;
            }
            
            // 2. 实现API限流机制
            String rateLimitKey = RATE_LIMIT_KEY_PREFIX + appId + ":" + fromUser;
            Long requestCount = redisTemplate.opsForValue().increment(rateLimitKey);
            
            if (requestCount == 1) {
                // 设置过期时间为1分钟
                redisTemplate.expire(rateLimitKey, RATE_LIMIT_EXPIRE, TimeUnit.SECONDS);
            }
            
            if (requestCount > RATE_LIMIT_MAX) {
                log.warn("用户触发限流 - appId: {}, fromUser: {}, 请求次数: {}", appId, fromUser, requestCount);
                return "当前AI服务访问频繁，请稍后再试（每分钟最多可请求2次）";
            }
            
            // 3. 调用真实AI服务
            long startTime = System.currentTimeMillis();
            log.info("开始调用AI模型 - appId: {}, fromUser: {}, message: {}, messageLength: {}", 
                    appId, fromUser, message, message.length());
            
            // 创建Prompt对象
            org.springframework.ai.chat.prompt.Prompt prompt = new org.springframework.ai.chat.prompt.Prompt(
                    new SystemMessage(SYSTEM_PROMPT),
                    new UserMessage(message)
            );
            
            // 调用AI模型
            org.springframework.ai.chat.model.ChatResponse chatResponse = chatModel.call(prompt);
            
            long endTime = System.currentTimeMillis();
            log.info("AI调用完成，耗时: {}ms", endTime - startTime);
            
            // 4. 处理AI响应
            String aiReplyContent = null;
            if (chatResponse != null && chatResponse.getResult() != null && chatResponse.getResult().getOutput() != null) {
                aiReplyContent = chatResponse.getResult().getOutput().getText();
                log.info("AI回复内容长度: {}字符", aiReplyContent.length());
                log.debug("AI回复内容: {}", aiReplyContent);
            }
            
            // 检查AI回复内容是否为空
            if (StringUtils.isBlank(aiReplyContent)) {
                log.error("AI返回空回复 - appId: {}, fromUser: {}, message: {}", appId, fromUser, message);
                return "AI服务存在问题，请检查连接或稍后重试";
            }
            
            // 5. 缓存AI回复
            redisTemplate.opsForValue().set(cacheKey, aiReplyContent, CACHE_EXPIRE, TimeUnit.SECONDS);
            
            // 6. 更新数据库记录
            aiReplyRecord.setReplyMessage(aiReplyContent);
            aiReplyRecord.setReplyStatus(WxAiReplyStatusEnum.REPLIED.getValue());
            this.lambdaUpdate()
                    .eq(AiReplyRecord::getId, aiReplyRecord.getId())
                    .set(AiReplyRecord::getReplyMessage, aiReplyContent)
                    .set(AiReplyRecord::getReplyStatus, WxAiReplyStatusEnum.REPLIED.getValue())
                    .update();
            
            return aiReplyContent;
        } catch (Exception e) {
            log.error("AI调用失败 - 错误类型: {}, 错误信息: {}, appId: {}, fromUser: {}, message: {}", 
                    e.getClass().getSimpleName(), e.getMessage(), appId, fromUser, message, e);
            return "AI服务存在问题，请检查连接或稍后重试";
        }
    }
}




