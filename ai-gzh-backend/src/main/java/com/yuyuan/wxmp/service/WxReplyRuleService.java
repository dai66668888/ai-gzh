package com.yuyuan.wxmp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuyuan.wxmp.model.dto.wxmpreplyrule.WxReplyContentDTO;
import com.yuyuan.wxmp.model.dto.wxmpreplyrule.WxReplyRulePageQueryRequest;
import com.yuyuan.wxmp.model.entity.WxReplyRule;
import com.yuyuan.wxmp.model.enums.WxReplyContentTypeEnum;
import com.yuyuan.wxmp.model.vo.WxReplyRuleVO;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

import java.util.List;

/**
 * @author cq
 * @description 针对表【wx_reply_rule(微信公众号回复规则)】的数据库操作Service
 * @createDate 2025-03-13 16:52:40
 */
public interface WxReplyRuleService extends IService<WxReplyRule> {

    /**
     * 获取查询包装器
     *
     * @param wxReplyRulePageQueryRequest 回复规则分页查询DTO
     * @return {@link QueryWrapper}<{@link WxReplyRule}>
     */
    QueryWrapper<WxReplyRule> getQueryWrapper(WxReplyRulePageQueryRequest wxReplyRulePageQueryRequest);

    /**
     * 查询分页
     *
     * @param wxReplyRulePage 回复规则分页数据
     * @param queryWrapper    查询包装器
     * @return {@link Page }<{@link WxReplyRuleVO }>
     */
    Page<WxReplyRuleVO> getPage(Page<WxReplyRule> wxReplyRulePage, QueryWrapper<WxReplyRule> queryWrapper);

    /**
     * 收到消息回复
     *
     * @param appId 应用 ID
     * @param msg   消息
     * @return {@link List }<{@link WxReplyContentDTO }>
     */
    WxReplyContentDTO receiveMessageReply(String appId, String msg);

    /**
     * 收到关注回复
     *
     * @param appId 应用 ID
     * @return {@link WxReplyContentDTO }
     */
    WxReplyContentDTO replySubscribe(String appId);

    /**
     * 根据内容类型回复
     *
     * @param wxMpXmlMessage  wxMpXmlMessage
     * @param replyContent    回复内容
     * @param contentTypeEnum 内容类型枚举
     * @return {@link WxMpXmlOutMessage }
     */
    WxMpXmlOutMessage replyByContentType(WxMpXmlMessage wxMpXmlMessage, WxReplyContentDTO replyContent, WxReplyContentTypeEnum contentTypeEnum);
}
