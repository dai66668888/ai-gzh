package com.yuyuan.wxmp.receive;

import com.yuyuan.wxmp.constant.MqConstant;
import com.yuyuan.wxmp.model.entity.WxAccount;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SyncMpConfigReceiver {

    @Resource
    private WxMpService wxMpService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "true"),
                    exchange = @Exchange(
                            name = MqConstant.ADD_WXMP_CONFIG_EXCHANGE,
                            // 指定为 FANOUT 类型
                            type = ExchangeTypes.FANOUT
                    )
            ))
    public void addMpServiceConfig(WxAccount wxAccount) {
        String appId = wxAccount.getAppId();
        WxMpDefaultConfigImpl config = wxAccount.toWxMpConfigStorage();
        try {
            wxMpService.addConfigStorage(appId, config);
        } catch (NullPointerException e) {
            log.info("初始化configStorageMap...");
            Map<String, WxMpConfigStorage> configStorages = new HashMap<>(6);
            configStorages.put(appId, config);
            wxMpService.setMultiConfigStorages(configStorages, appId);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "true"),
                    exchange = @Exchange(
                            name = MqConstant.REMOVE_WXMP_CONFIG_EXCHANGE,
                            // 指定为 FANOUT 类型
                            type = ExchangeTypes.FANOUT
                    )
            ))
    public void removeMpServiceConfig(List<String> appIds) {
        appIds.forEach(wxMpService::removeConfigStorage);
    }

}