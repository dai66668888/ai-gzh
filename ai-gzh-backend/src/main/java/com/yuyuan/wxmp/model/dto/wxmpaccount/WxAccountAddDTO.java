package com.yuyuan.wxmp.model.dto.wxmpaccount;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 微信公众号账号
 * @TableName wx_account
 * @author cq
 */
@Data
public class WxAccountAddDTO implements Serializable {

    /**
     * appid
     */
    @Schema(description = "公众号appid", requiredMode = Schema.RequiredMode.REQUIRED)
    private String appId;

    /**
     * 公众号名称
     */
    @Schema(description = "公众号名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * appSecret
     */
    @Schema(description = "密钥", requiredMode = Schema.RequiredMode.REQUIRED)
    private String secret;

    /**
     * token
     */
    @Schema(description = "token", requiredMode = Schema.RequiredMode.REQUIRED)
    private String token;

    /**
     * aesKey
     */
    @Schema(description = "aesKey", requiredMode = Schema.RequiredMode.REQUIRED)
    private String aesKey;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}