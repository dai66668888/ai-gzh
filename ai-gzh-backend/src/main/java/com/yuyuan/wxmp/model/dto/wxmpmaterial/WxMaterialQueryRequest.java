package com.yuyuan.wxmp.model.dto.wxmpmaterial;


import com.yuyuan.wxmp.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WxMaterialQueryRequest extends PageRequest {
    private String materialType;
}
