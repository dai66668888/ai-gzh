package com.yuyuan.wxmp.model.vo;


import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yuyuan.wxmp.model.dto.wxmpreplyrule.WxReplyContentDTO;
import com.yuyuan.wxmp.model.dto.wxmpreplyrule.WxReplyMatchValueDTO;
import com.yuyuan.wxmp.model.entity.WxReplyRule;
import com.yuyuan.wxmp.utils.CopyUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 微信公众号回复规则
 *
 * @TableName wx_reply_rule
 */
@TableName(value = "wx_reply_rule")
@Data
public class WxReplyRuleVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * appid
     */
    private String appId;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 匹配值（关键字或者事件的key）
     */
    @Schema(name = "匹配值（关键字或者事件的key）", description = "这里返回的是列表，前端根据情况展示")
    private List<WxReplyMatchValueDTO> matchValue;

    /**
     * 回复内容
     */
    private WxReplyContentDTO replyContent;

    /**
     * 规则描述
     */
    private String ruleDescription;

    /**
     * 0 为关键词触发、1 为默认触发、2 为被关注触发、3 为菜单点击事件类型
     */
    private Integer replyType;


    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建人信息
     */
    private UserVO user;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public static WxReplyRuleVO obj2VO(WxReplyRule wxReplyRule) {
        WxReplyRuleVO wxReplyRuleVO = CopyUtil.copy(wxReplyRule, WxReplyRuleVO.class);
        if (StringUtils.isNotBlank(wxReplyRule.getMatchValue())) {
            wxReplyRuleVO.setMatchValue(JSONUtil.toList(wxReplyRule.getMatchValue(), WxReplyMatchValueDTO.class));
        }
        if (StringUtils.isNotBlank(wxReplyRule.getReplyContent())) {
            wxReplyRuleVO.setReplyContent(JSONUtil.toBean(wxReplyRule.getReplyContent(), WxReplyContentDTO.class));
        }
        return wxReplyRuleVO;
    }
}