package com.yuyuan.wxmp.handler;


import com.yuyuan.wxmp.model.dto.wxmpreplyrule.WxReplyContentDTO;
import com.yuyuan.wxmp.model.enums.WxReplyContentTypeEnum;
import com.yuyuan.wxmp.service.WxReplyRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 关注处理器
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 **/
@Component
@RequiredArgsConstructor
@Slf4j
public class SubscribeHandler implements WxMpMessageHandler {

    private final WxReplyRuleService wxReplyRuleService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map,
                                    WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        log.info("收到关注事件：fromUser={}, toUser={}", wxMpXmlMessage.getFromUser(), wxMpXmlMessage.getToUser());
        String appId = WxMpConfigStorageHolder.get();
        log.info("处理关注事件：appId={}", appId);
        
        WxReplyContentDTO replyContent = wxReplyRuleService.replySubscribe(appId);
        WxMpXmlOutTextMessage defaultReply = WxMpXmlOutMessage.TEXT().content("感谢关注")
                .fromUser(wxMpXmlMessage.getToUser())
                .toUser(wxMpXmlMessage.getFromUser())
                .build();
        
        if (ObjectUtils.isEmpty(replyContent)) {
            log.info("未找到关注回复规则，返回默认回复");
            return defaultReply;
        }
        
        log.info("找到关注回复规则，contentType={}", replyContent.getContentType());
        WxReplyContentTypeEnum contentTypeEnum = WxReplyContentTypeEnum.getEnumByValue(replyContent.getContentType());
        if (ObjectUtils.isEmpty(contentTypeEnum)) {
            log.warn("未知的回复内容类型：{}", replyContent.getContentType());
            return defaultReply;
        }
        
        WxMpXmlOutMessage result = wxReplyRuleService.replyByContentType(wxMpXmlMessage, replyContent, contentTypeEnum);
        log.info("生成关注回复消息成功");
        return result;
    }
}
