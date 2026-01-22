package com.yuyuan.wxmp.config;

import com.yuyuan.wxmp.handler.MessageHandler;
import com.yuyuan.wxmp.handler.SubscribeHandler;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts.EventType;
import me.chanjar.weixin.common.api.WxConsts.XmlMsgType;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信公众号路由配置
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Configuration
@Slf4j
public class WxMpMsgRouterConfig {

    @Resource
    private WxMpService wxMpService;

    @Resource
    private MessageHandler messageHandler;

    @Resource
    private SubscribeHandler subscribeHandler;

    @Bean
    public WxMpMessageRouter messageRouter() {
        WxMpMessageRouter router = new WxMpMessageRouter(wxMpService);
        // 添加路由日志过滤器
        router.rule()
                .async(false)
                .handler((wxMpXmlMessage, map, wxMpService, wxSessionManager) -> {
                    log.info("开始路由消息 - 消息类型: {}, 内容: {}", wxMpXmlMessage.getMsgType(), wxMpXmlMessage.getContent());
                    return null;
                })
                .next();
        // 消息
        router.rule()
                .async(false)
                .msgType(XmlMsgType.TEXT)
                .handler(messageHandler)
                .end();
        // 关注
        router.rule()
                .async(false)
                .msgType(XmlMsgType.EVENT)
                .event(EventType.SUBSCRIBE)
                .handler(subscribeHandler)
                .end();
        return router;
    }
}
