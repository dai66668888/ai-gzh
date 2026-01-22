package com.yuyuan.wxmp.model.dto.wxmpaccount;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 微信公众号账号
 *
 * @author cq
 * @TableName wx_account
 */
@Data
public class WxAccountUpdateDTO implements Serializable {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * appid
     */
    @Schema(description = "appid")
    private String appId;

    /**
     * 公众号名称
     */
    @Schema(description = "公众号名称")
    private String name;

    /**
     * appSecret
     */
    @Schema(description = "密钥")
    private String secret;

    /**
     * token
     */
    @Schema(description = "token")
    private String token;

    /**
     * aesKey
     */
    @Schema(description = "aesKey")
    private String aesKey;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}