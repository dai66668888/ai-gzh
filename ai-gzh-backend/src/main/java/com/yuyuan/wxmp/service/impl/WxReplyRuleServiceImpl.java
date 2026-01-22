package com.yuyuan.wxmp.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuyuan.wxmp.common.ErrorCode;
import com.yuyuan.wxmp.common.PageRequest;
import com.yuyuan.wxmp.exception.BusinessException;
import com.yuyuan.wxmp.mapper.WxReplyRuleMapper;
import com.yuyuan.wxmp.model.dto.wxmpreplyrule.WxReplyContentDTO;
import com.yuyuan.wxmp.model.dto.wxmpreplyrule.WxReplyMatchValueDTO;
import com.yuyuan.wxmp.model.dto.wxmpreplyrule.WxReplyRulePageQueryRequest;
import com.yuyuan.wxmp.model.entity.User;
import com.yuyuan.wxmp.model.entity.WxReplyRule;
import com.yuyuan.wxmp.model.enums.WxReplyContentTypeEnum;
import com.yuyuan.wxmp.model.enums.WxReplyMatchTypeEnum;
import com.yuyuan.wxmp.model.enums.WxReplyRuleTypeEnum;
import com.yuyuan.wxmp.model.vo.WxReplyRuleVO;
import com.yuyuan.wxmp.service.UserService;
import com.yuyuan.wxmp.service.WxReplyRuleService;
import com.yuyuan.wxmp.utils.WrapperUtil;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author cq
 * @description 针对表【wx_reply_rule(微信公众号回复规则)】的数据库操作Service实现
 * @createDate 2025-03-13 16:52:40
 */
@Service
@RequiredArgsConstructor
public class WxReplyRuleServiceImpl extends ServiceImpl<WxReplyRuleMapper, WxReplyRule>
        implements WxReplyRuleService {

    private final UserService userService;

    @Override
    public Page<WxReplyRuleVO> getPage(Page<WxReplyRule> wxReplyRulePage, QueryWrapper<WxReplyRule> queryWrapper) {
        Page<WxReplyRule> replyRulePage = this.page(wxReplyRulePage, queryWrapper);
        Page<WxReplyRuleVO> resultPage = new Page<>();
        List<WxReplyRule> wxReplyRuleList = replyRulePage.getRecords();


        Set<Long> userIdSet = wxReplyRuleList.stream().map(WxReplyRule::getUserId).collect(Collectors.toSet());
        Map<Long, User> userIdUserListMap;
        if (ObjectUtils.isNotEmpty(userIdSet)) {
            userIdUserListMap = userService.listByIds(userIdSet)
                    .stream()
                    .collect(Collectors.toMap(User::getId, user -> user));
        } else {
            userIdUserListMap = new HashMap<>();
        }
        BeanUtils.copyProperties(replyRulePage, resultPage, "records");

        resultPage.setRecords(
                wxReplyRuleList
                        .stream()
                        .map(wxReplyRule -> {
                            WxReplyRuleVO replyRuleVO = WxReplyRuleVO.obj2VO(wxReplyRule);
                            User createUser = userIdUserListMap.get(wxReplyRule.getUserId());
                            if (ObjectUtils.isNotEmpty(createUser)) {
                                replyRuleVO.setUser(userService.getUserVO(createUser));
                            }
                            return replyRuleVO;
                        })
                        .collect(Collectors.toList())
        );
        return resultPage;
    }


    @Override
    public QueryWrapper<WxReplyRule> getQueryWrapper(WxReplyRulePageQueryRequest wxReplyRulePageQueryRequest) {
        if (wxReplyRulePageQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String appId = wxReplyRulePageQueryRequest.getAppId();
        String ruleName = wxReplyRulePageQueryRequest.getRuleName();
        String matchValue = wxReplyRulePageQueryRequest.getMatchValue();
        String replyContent = wxReplyRulePageQueryRequest.getReplyContent();
        String eventKey = wxReplyRulePageQueryRequest.getEventKey();
        String ruleDescription = wxReplyRulePageQueryRequest.getRuleDescription();
        Integer replyType = wxReplyRulePageQueryRequest.getReplyType();
        String sortField = wxReplyRulePageQueryRequest.getSortField();
        String sortOrder = wxReplyRulePageQueryRequest.getSortOrder();
        List<PageRequest.Sorter> sorterList = wxReplyRulePageQueryRequest.getSorterList();

        QueryWrapper<WxReplyRule> queryWrapper = Wrappers.query();
        queryWrapper.eq(StringUtils.isNotBlank(appId), "appId", appId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(replyType), "replyType", replyType);
        queryWrapper.like(ObjectUtils.isNotEmpty(eventKey), "eventKey", eventKey);
        queryWrapper.like(StringUtils.isNotBlank(ruleName), "ruleName", ruleName);
        queryWrapper.like(StringUtils.isNotBlank(matchValue), "matchValue", matchValue);
        queryWrapper.like(StringUtils.isNotBlank(replyContent), "replyContent", replyContent);
        queryWrapper.like(StringUtils.isNotBlank(ruleDescription), "ruleDescription", ruleDescription);
        WrapperUtil.handleOrder(queryWrapper, sorterList, sortField, sortOrder);
        return queryWrapper;
    }


    @Override
    public WxReplyContentDTO receiveMessageReply(String appId, String msg) {
        // 1、将所有的关键词规则和默认规则都查询出来
        List<WxReplyRule> wxReplyRuleList = this.list(
                Wrappers.lambdaQuery(WxReplyRule.class)
                        .eq(WxReplyRule::getAppId, appId)
                        .in(
                                WxReplyRule::getReplyType,
                                WxReplyRuleTypeEnum.KEYWORDS.getValue(), WxReplyRuleTypeEnum.DEFAULT.getValue()
                        )
                        .orderByDesc(WxReplyRule::getUpdateTime)
        );

        // 2、将关键词规则都过滤出来
        WxReplyRule keyWordReply = wxReplyRuleList
                .stream()
                .filter(wxReplyRule -> {
                    if (!WxReplyRuleTypeEnum.KEYWORDS.getValue().equals(wxReplyRule.getReplyType())) {
                        return false;
                    }
                    try {
                        List<WxReplyMatchValueDTO> keywords = JSONUtil.toList(wxReplyRule.getMatchValue(), WxReplyMatchValueDTO.class);
                        // 查看当前规则的关键字是否包含在用户发送的信息中
                        for (WxReplyMatchValueDTO keyword : keywords) {
                            String matchKeyWords = keyword.getMatchKeyWords();
                            Integer matchType = keyword.getMatchType();
                            if (StringUtils.isBlank(matchKeyWords)) {
                                continue;
                            }
                            if (WxReplyMatchTypeEnum.ALL.getValue().equals(matchType) && matchKeyWords.equalsIgnoreCase(msg)) {
                                return true;
                            }
                            if (WxReplyMatchTypeEnum.LIKE.getValue().equals(matchType) && msg.toLowerCase().contains(matchKeyWords.toLowerCase())) {
                                return true;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("解析匹配规则失败：规则ID=" + wxReplyRule.getId() + ", matchValue=" + wxReplyRule.getMatchValue() + ", 错误=" + e.getMessage());
                    }
                    return false;
                })
                .findFirst()
                .orElse(null);

        // 3、如果没有匹配上的关键词，则返回默认回复内容
        if (ObjectUtils.isEmpty(keyWordReply)) {
            List<WxReplyRule> defaultList = wxReplyRuleList
                    .stream()
                    .filter(wxReplyRule -> WxReplyRuleTypeEnum.DEFAULT.getValue().equals(wxReplyRule.getReplyType()))
                    .collect(Collectors.toList());

            // 4、如果默认规则也没有，则返回程序指定的文案
            if (ObjectUtils.isEmpty(defaultList)) {
                return null;
            }
            return JSONUtil.toBean(RandomUtil.randomEle(defaultList).getReplyContent(), WxReplyContentDTO.class);
        }

        // 5、直接回复
        return JSONUtil.toBean(keyWordReply.getReplyContent(), WxReplyContentDTO.class);
    }

    @Override
    public WxReplyContentDTO replySubscribe(String appId) {
        List<WxReplyRule> wxReplyRuleList = this.list(
                Wrappers.lambdaQuery(WxReplyRule.class)
                        .eq(WxReplyRule::getAppId, appId)
                        .eq(WxReplyRule::getReplyType, WxReplyRuleTypeEnum.SUBSCRIBE.getValue())
        );
        if (ObjectUtils.isEmpty(wxReplyRuleList)) {
            return null;
        }
        // 随机选择一个
        return JSONUtil.toBean(RandomUtil.randomEle(wxReplyRuleList).getReplyContent(), WxReplyContentDTO.class);
    }

    @Override
    public WxMpXmlOutMessage replyByContentType(WxMpXmlMessage wxMpXmlMessage, WxReplyContentDTO replyContent, WxReplyContentTypeEnum contentTypeEnum) {
        return switch (contentTypeEnum) {
            case TEXT -> WxMpXmlOutMessage
                    .TEXT()
                    .content(replyContent.getTextContent())
                    .fromUser(wxMpXmlMessage.getToUser())
                    .toUser(wxMpXmlMessage.getFromUser())
                    .build();
            case IMAGE -> WxMpXmlOutMessage
                    .IMAGE()
                    .mediaId(replyContent.getMediaId())
                    .fromUser(wxMpXmlMessage.getToUser())
                    .toUser(wxMpXmlMessage.getFromUser())
                    .build();
            case VOICE -> WxMpXmlOutMessage
                    .VOICE()
                    .mediaId(replyContent.getMediaId())
                    .fromUser(wxMpXmlMessage.getToUser())
                    .toUser(wxMpXmlMessage.getFromUser())
                    .build();
            case VIDEO -> WxMpXmlOutMessage
                    .VIDEO()
                    .mediaId(replyContent.getMediaId())
                    .fromUser(wxMpXmlMessage.getToUser())
                    .toUser(wxMpXmlMessage.getFromUser())
                    .build();
        };
    }
}




