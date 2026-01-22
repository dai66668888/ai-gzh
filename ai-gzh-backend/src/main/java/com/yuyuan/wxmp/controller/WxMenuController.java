package com.yuyuan.wxmp.controller;


import com.yuyuan.wxmp.common.BaseResponse;
import com.yuyuan.wxmp.common.ResultUtils;
import com.yuyuan.wxmp.model.enums.WxMenuButtonTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.menu.WxMpMenu;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 微信公众号菜单管理
 *
 * @author cq
 * @since 2023/11/22
 */
@RestController
@RequestMapping("/wx/menu")
@RequiredArgsConstructor
//@Api(tags = "公众号菜单管理")
@Slf4j
public class WxMenuController {
    private final WxMpService wxMpService;


    @GetMapping("/get/type")
    @Operation(summary = "获取菜单类型")
    public BaseResponse<WxMenuButtonTypeEnum[]> getMenuType() {
        return ResultUtils.success(WxMenuButtonTypeEnum.values());
    }

    /**
     * 获取公众号菜单
     */
    @GetMapping("/get/{appId}")
    @Operation(summary = "获取公众号菜单")
    public BaseResponse<WxMpMenu> getMenu(@PathVariable String appId) throws WxErrorException {
//        wxMpService.switchoverTo(appId);
        return ResultUtils.success(wxMpService.getMenuService().menuGet());
    }

    /**
     * 创建、更新菜单
     */
    @PostMapping("/update/{appId}")
    @Operation(summary = "更新菜单")
    public BaseResponse<Boolean> updateMenu(@PathVariable String appId, @RequestBody List<WxMenuButton> wxMenuButtonList) throws WxErrorException {
//        wxMpService.switchoverTo(appId);
        WxMenu wxMenu = new WxMenu();
        wxMenu.setButtons(wxMenuButtonList);
        wxMpService.getMenuService().menuCreate(wxMenu);
        return ResultUtils.success(true);
    }


    @PostMapping("/delete/{appId}")
    @Operation(summary = "删除菜单", description = "注意这里是全部删除")
    public BaseResponse<Boolean> deleteMenu(@PathVariable String appId) throws WxErrorException {
//        wxMpService.switchoverTo(appId);
        wxMpService.getMenuService().menuDelete();
        return ResultUtils.success(true);
    }
}
