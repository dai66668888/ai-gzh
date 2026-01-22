package com.yuyuan.wxmp.handler;


import com.yuyuan.wxmp.constant.RedisConstant;
import com.yuyuan.wxmp.manager.DistributedLockManager;
import cn.hutool.crypto.digest.DigestUtil;
import com.yuyuan.wxmp.model.dto.wxmpreplyrule.WxReplyContentDTO;
import com.yuyuan.wxmp.model.entity.AiReplyRecord;
import com.yuyuan.wxmp.model.enums.WxAiReplyStatusEnum;
import com.yuyuan.wxmp.model.enums.WxReplyContentTypeEnum;
import com.yuyuan.wxmp.service.AiReplyRecordService;
import com.yuyuan.wxmp.service.WxReplyRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * 消息处理器
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 **/
@Component
@RequiredArgsConstructor
@Slf4j
public class MessageHandler implements WxMpMessageHandler {

    private final WxReplyRuleService wxReplyRuleService;

    private final AiReplyRecordService aiReplyRecordService;

    private final DistributedLockManager distributedLockManager;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map,
                                    WxMpService wxMpService, WxSessionManager wxSessionManager) {
        String appId = WxMpConfigStorageHolder.get();
        String userMessage = wxMpXmlMessage.getContent();
        String fromUser = wxMpXmlMessage.getFromUser();
        String toUser = wxMpXmlMessage.getToUser();
        
        // 针对公众号和用户加锁，避免用户短时间内发送同一条信息导致 AI 回复了多次
        String lock = RedisConstant.MESSAGE_REPLY_LOCK + appId + ":" + fromUser + ":" + DigestUtil.md5Hex(userMessage);
        
        return distributedLockManager.nonBlockExecute(lock, 
                // 获取锁成功时执行
                () -> {
                    try {
                        // 1. 优先匹配自动回复规则
                        WxReplyContentDTO replyContent = wxReplyRuleService.receiveMessageReply(appId, userMessage);
                        String finalReplyContent;
                        
                        if (ObjectUtils.isNotEmpty(replyContent)) {
                            // 1.1 匹配到规则，直接回复
                            WxReplyContentTypeEnum contentTypeEnum = WxReplyContentTypeEnum.getEnumByValue(replyContent.getContentType());
                            if (ObjectUtils.isEmpty(contentTypeEnum)) {
                                finalReplyContent = "抱歉，我暂时无法理解您的问题。您可以尝试问其他问题，或者提供更多详细信息。";
                            } else {
                                // 直接获取文本内容，简化处理
                                finalReplyContent = replyContent.getTextContent();
                            }
                        } else {
                            // 2. 没有匹配到自动回复规则，调用AI
                            AiReplyRecord replyRecord = aiReplyRecordService.lambdaQuery()
                                    .eq(AiReplyRecord::getFromUser, fromUser)
                                    .eq(AiReplyRecord::getAppId, appId)
                                    .eq(AiReplyRecord::getMessage, userMessage)
                                    .eq(AiReplyRecord::getReplyStatus, WxAiReplyStatusEnum.NOT_REPLY.getValue())
                                    .one();

                            if (ObjectUtils.isEmpty(replyRecord)) {
                                AiReplyRecord aiReplyRecord = new AiReplyRecord();
                                aiReplyRecord.setAppId(appId);
                                aiReplyRecord.setFromUser(fromUser);
                                aiReplyRecord.setMessage(userMessage);
                                aiReplyRecordService.save(aiReplyRecord);
                                
                                finalReplyContent = aiReplyRecordService.aiReply(appId, fromUser, userMessage, aiReplyRecord);
                            } else if (ObjectUtils.isEmpty(replyRecord.getReplyMessage())) {
                                // 如果回复消息为空，代表此时没有 AI 回复，需要重新调用AI
                                log.info("找到未回复的记录，重新调用AI服务 - 记录ID: {}, 消息: {}", replyRecord.getId(), replyRecord.getMessage());
                                finalReplyContent = aiReplyRecordService.aiReply(appId, fromUser, userMessage, replyRecord);
                            } else {
                                // 3. AI已经回复过，直接使用
                                finalReplyContent = replyRecord.getReplyMessage();
                                aiReplyRecordService.lambdaUpdate()
                                        .set(AiReplyRecord::getReplyStatus, WxAiReplyStatusEnum.REPLIED.getValue())
                                        .eq(AiReplyRecord::getId, replyRecord.getId())
                                        .update();
                            }
                        }
                        
                        // 返回最终回复给微信服务器
                        return WxMpXmlOutMessage.TEXT()
                                .content(finalReplyContent)
                                .fromUser(toUser)
                                .toUser(fromUser)
                                .build();
                    } catch (Exception e) {
                        log.error("处理消息失败 - fromUser: {}, message: {}, 错误: {}", fromUser, userMessage, e.getMessage(), e);
                        return WxMpXmlOutMessage.TEXT()
                                .content("抱歉，我暂时无法处理您的请求，请稍后再试。")
                                .fromUser(toUser)
                                .toUser(fromUser)
                                .build();
                    }
                },
                // 获取锁失败时执行
                () -> {
                    log.info("获取锁失败，返回默认回复 - appId: {}, fromUser: {}, message: {}", appId, fromUser, userMessage);
                    return WxMpXmlOutMessage.TEXT()
                            .content("正在处理您的请求，请稍后再试。")
                            .fromUser(toUser)
                            .toUser(fromUser)
                            .build();
                }
        );
    }
}
