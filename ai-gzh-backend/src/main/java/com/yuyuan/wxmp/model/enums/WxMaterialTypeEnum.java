package com.yuyuan.wxmp.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.chanjar.weixin.common.api.WxConsts;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * wx 材质类型枚举
 *
 * @author cq
 * @since 2025/03/13
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum WxMaterialTypeEnum {

    IMAGE("图片", WxConsts.MaterialType.IMAGE),
    VOICE("音频", WxConsts.MaterialType.VOICE),
    VIDEO("视频", WxConsts.MaterialType.VIDEO),
//    NEWS("图文", WxConsts.MaterialType.NEWS),
    ;
    @JsonProperty("label")
    private final String text;

    private final String value;

    WxMaterialTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static WxMaterialTypeEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (WxMaterialTypeEnum anEnum : WxMaterialTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
