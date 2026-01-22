package com.yuyuan.wxmp.model.dto.wxmpreplyrule;


import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.yuyuan.wxmp.model.entity.WxReplyRule;
import com.yuyuan.wxmp.utils.CopyUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 微信公众号回复规则
 *
 * @TableName wx_reply_rule
 */
@Data
public class WxReplyRuleAddRequest implements Serializable {

    /**
     * appid
     */
    @Schema(description = "公众号appid", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "appId 不能为空")
    private String appId;

    /**
     * 规则名称
     */
    @Schema(description = "规则名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;

    /**
     * 匹配值（关键字或者事件的key）
     */
    @Schema(description = "匹配值（关键字的key）,如果是收到消息回复类型的则不需要填写，如果是菜单栏点击事件也是列表形式传输，只需要传一个元素即可")
    private List<WxReplyMatchValueDTO> matchValue;

    /**
     * 菜单栏点击事件的key
     */
    @Schema(description = "菜单栏点击事件的key")
    private String eventKey;

    /**
     * 回复内容
     */
    @Schema(description = "回复内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "回复内容不能为空")
    private WxReplyContentDTO replyContent;

    /**
     * 规则描述
     */
    @Schema(description = "规则描述")
    private String ruleDescription;

    /**
     * 0 为关键词触发、1 为默认触发、2 为被关注触发、3 为菜单点击事件类型
     */
    @Schema(description = "0 为关键词触发、1 为默认触发、2 为被关注触发、3 为菜单点击事件类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "回复类型不能为空")
    private Integer replyType;


    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    public WxReplyRule toWxReplyRule() {
        WxReplyRule wxReplyRule = CopyUtil.copy(this, WxReplyRule.class);
        wxReplyRule.setReplyContent(JSONUtil.toJsonStr(replyContent));
        if (ObjectUtils.isNotEmpty(matchValue)) {
            wxReplyRule.setMatchValue(JSONUtil.toJsonStr(matchValue));
        }
        return wxReplyRule;
    }
}