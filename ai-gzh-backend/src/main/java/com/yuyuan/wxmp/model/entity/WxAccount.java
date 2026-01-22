package com.yuyuan.wxmp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 微信公众号账号
 * @TableName wx_account
 */
@TableName(value ="wx_account")
@Data
public class WxAccount implements Serializable {
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
     * token
     */
    private String token;

    /**
     * aesKey
     */
    private String aesKey;

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

    public WxMpDefaultConfigImpl toWxMpConfigStorage(){
        WxMpDefaultConfigImpl configStorage = new WxMpDefaultConfigImpl();
        configStorage.setAppId(appId);
        configStorage.setSecret(secret);
        configStorage.setToken(token);
        configStorage.setAesKey(aesKey);
        return configStorage;
    }
}