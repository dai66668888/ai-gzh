package com.yuyuan.wxmp.model.dto.wxmpaccount;

import com.baomidou.mybatisplus.annotation.TableField;
import com.yuyuan.wxmp.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 微信公众号账号
 *
 * @author cq
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WxAccountPageQueryDTO extends PageRequest implements Serializable {
    /**
     * appid
     */
    @Schema(description = "公众号appid")
    private String appId;

    /**
     * 公众号名称
     */
    @Schema(description = "公众号名称")
    private String name;

    /**
     * 认证状态
     */
    @Schema(description = "认证状态")
    private Boolean verified;

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