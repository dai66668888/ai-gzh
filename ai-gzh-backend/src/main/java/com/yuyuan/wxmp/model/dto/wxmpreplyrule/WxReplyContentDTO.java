package com.yuyuan.wxmp.model.dto.wxmpreplyrule;


import com.yuyuan.wxmp.model.enums.WxReplyContentTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * WX 回复内容 DTO
 *
 * @author cq
 * @since 2023/11/23
 */
@Data
public class WxReplyContentDTO {

    /**
     * 内容类型
     * 参考{@link WxReplyContentTypeEnum}
     */
    @Schema(description = "回复内容类型，默认为文字类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer contentType = 0;

    @Schema(description = "图文消息的id，可以调用获取图文消息接口获取")
    private String articleId;

    @Schema(description = "素材Id（图片、音频、视频等）")
    private String mediaId;

    @Schema(description = "文本消息内容")
    private String textContent;
}
