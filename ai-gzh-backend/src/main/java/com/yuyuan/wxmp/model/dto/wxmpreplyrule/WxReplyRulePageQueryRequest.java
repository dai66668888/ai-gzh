package com.yuyuan.wxmp.model.dto.wxmpreplyrule;


import com.baomidou.mybatisplus.annotation.TableField;
import com.yuyuan.wxmp.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 微信公众号账号
 * @TableName wx_account
 * @author cq
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WxReplyRulePageQueryRequest extends PageRequest implements Serializable {
    /**
     * appid
     */
    @Schema(name = "公众号appid")
    private String appId;

    /**
     * 规则名称
     */
    @Schema(name = "规则名称")
    private String ruleName;

    /**
     * 匹配值（关键字或者事件的key）
     */
    @Schema(name = "关键字搜索")
    private String matchValue;

    @Schema(name = "菜单栏点击事件key")
    private String eventKey;

    /**
     * 回复内容
     */
    @Schema(name = "回复内容")
    private String replyContent;

    /**
     * 规则描述
     */
    @Schema(name = "规则描述")
    private String ruleDescription;

    /**
     * 0 为关键词触发、1 为默认触发、2 为被关注触发、3 为菜单点击事件类型
     */
    @Schema(description = "0 为关键词触发、1 为默认触发、2 为被关注触发、3 为菜单点击事件类型")
    private Integer replyType;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}