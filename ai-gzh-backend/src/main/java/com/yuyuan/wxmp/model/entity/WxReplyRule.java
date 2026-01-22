package com.yuyuan.wxmp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 微信公众号回复规则
 * @TableName wx_reply_rule
 */
@TableName(value ="wx_reply_rule")
@Data
public class WxReplyRule implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
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
    private String matchValue;

    /**
     * 菜单栏点击事件的key
     */
    private String eventKey;

    /**
     * 回复内容（json）
     */
    private String replyContent;

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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}