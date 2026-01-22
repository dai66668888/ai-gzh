package com.yuyuan.wxmp.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 微信公众号账号
 * @author cq
 */
@Data
public class WxAccountVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * appid
     */
    private String appId;

    /**
     * 公众号名称
     */
    private String name;

    /**
     * 认证状态
     */
    private Boolean verified;

    /**
     * appSecret
     */
    private String secret;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建人信息
     */
    private UserVO user;

    /**
     * token
     */
    private String token;

    /**
     * aesKey
     */
    private String aesKey;

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