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
 * AI 回复内容记录
 * @TableName ai_reply_record
 */
@TableName(value ="ai_reply_record")
@Data
public class AiReplyRecord implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 接收到消息的公众号 appId
     */
    private String appId;

    /**
     *  发送用户
     */
    private String fromUser;

    /**
     * 用户发送消息
     */
    private String message;

    /**
     * 回复消息
     */
    private String replyMessage;

    /**
     * 回复状态，0 - 未回复、1 - 已回复
     */
    private Integer replyStatus;

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
}