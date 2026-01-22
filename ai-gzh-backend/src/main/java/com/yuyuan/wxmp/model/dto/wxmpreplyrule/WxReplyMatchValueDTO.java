package com.yuyuan.wxmp.model.dto.wxmpreplyrule;


import com.yuyuan.wxmp.model.enums.WxReplyMatchTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * wx 回复匹配值
 *
 * @author cq
 * @since 2023/11/23
 */
@Data
public class WxReplyMatchValueDTO {
    /**
     * 匹配类型（全匹配还是半匹配）
     * {@link WxReplyMatchTypeEnum}
     */
    @Schema(description = "匹配类型（全匹配还是半匹配）,默认为模糊匹配")
    private Integer matchType = 1;

    @Schema(description = "关键字")
    private String matchKeyWords;
}
