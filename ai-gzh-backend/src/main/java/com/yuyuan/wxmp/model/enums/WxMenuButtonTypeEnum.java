package com.yuyuan.wxmp.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单按钮类型枚举
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum WxMenuButtonTypeEnum {

    VIEW("网页类型", "view"),
    CLICK("点击类型", "click"),
    MINIPROGRAM("小程序", "miniprogram");

    @JsonProperty("label")
    private final String text;

    private final String value;

    WxMenuButtonTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return {@link List}<{@link String}>
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 值
     * @return {@link WxMenuButtonTypeEnum}
     */
    public static WxMenuButtonTypeEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (WxMenuButtonTypeEnum anEnum : WxMenuButtonTypeEnum.values()) {
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
