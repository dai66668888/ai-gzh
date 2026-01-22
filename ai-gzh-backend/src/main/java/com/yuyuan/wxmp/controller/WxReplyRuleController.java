package com.yuyuan.wxmp.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuyuan.wxmp.common.BaseResponse;
import com.yuyuan.wxmp.common.ErrorCode;
import com.yuyuan.wxmp.common.ResultUtils;
import com.yuyuan.wxmp.exception.BusinessException;
import com.yuyuan.wxmp.exception.ThrowUtils;
import com.yuyuan.wxmp.model.dto.wxmpreplyrule.WxReplyRuleAddRequest;
import com.yuyuan.wxmp.model.dto.wxmpreplyrule.WxReplyRulePageQueryRequest;
import com.yuyuan.wxmp.model.dto.wxmpreplyrule.WxReplyRuleUpdateRequest;
import com.yuyuan.wxmp.model.entity.WxReplyRule;
import com.yuyuan.wxmp.model.enums.WxReplyContentTypeEnum;
import com.yuyuan.wxmp.model.enums.WxReplyMatchTypeEnum;
import com.yuyuan.wxmp.model.enums.WxReplyRuleTypeEnum;
import com.yuyuan.wxmp.model.vo.WxReplyRuleVO;
import com.yuyuan.wxmp.service.UserService;
import com.yuyuan.wxmp.service.WxReplyRuleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 自动回复规则
 *
 * @author cq
 * @since 2025/03/13
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/wx/reply")
//@Api(tags = "自动回复规则管理")
public class WxReplyRuleController {

    private final WxReplyRuleService wxReplyRuleService;

    private final UserService userService;


    @GetMapping("/get/reply/type")
    @Operation(summary = "获取回复规则类型")
    public BaseResponse<WxReplyRuleTypeEnum[]> getWxReplyType() {
        return ResultUtils.success(WxReplyRuleTypeEnum.values());
    }

    @GetMapping("/get/match/type")
    @Operation(summary = "获取关键字匹配类型")
    public BaseResponse<WxReplyMatchTypeEnum[]> getWxReplyMatchType() {
        return ResultUtils.success(WxReplyMatchTypeEnum.values());
    }

    @GetMapping("/get/content/type")
    @Operation(summary = "获取回复内容类型")
    public BaseResponse<WxReplyContentTypeEnum[]> getWxReplyContentType() {
        return ResultUtils.success(WxReplyContentTypeEnum.values());
    }


    /**
     * 列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询回复规则")
    public BaseResponse<Page<WxReplyRuleVO>> listWxMpReplyRuleByPage(WxReplyRulePageQueryRequest wxReplyRulePageQueryRequest) {
        long current = wxReplyRulePageQueryRequest.getCurrent();
        long size = wxReplyRulePageQueryRequest.getPageSize();
        return ResultUtils.success(wxReplyRuleService.getPage(new Page<>(current, size),
                wxReplyRuleService.getQueryWrapper(wxReplyRulePageQueryRequest)));
    }


    /**
     * 信息
     */
    @GetMapping("/get/vo")
    @Operation(summary = "回复规则详情")
    public BaseResponse<WxReplyRuleVO> getWxMpReplyRuleVOById(Long id) {
        WxReplyRule wxReplyRule = wxReplyRuleService.getById(id);

        ThrowUtils.throwIf(ObjectUtils.isEmpty(wxReplyRule), ErrorCode.NOT_FOUND_ERROR);

        WxReplyRuleVO replyRuleVO = WxReplyRuleVO.obj2VO(wxReplyRule);
        replyRuleVO.setUser(userService.getUserVO(userService.getById(wxReplyRule.getUserId())));
        return ResultUtils.success(replyRuleVO);
    }

    /**
     * 新增
     */
    @PostMapping("/add")
    @Operation(summary = "新增回复规则")
    public BaseResponse<Long> addWxReplyRule(@Valid @RequestBody WxReplyRuleAddRequest wxReplyRuleAddRequest, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(wxReplyRuleAddRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Integer replyType = wxReplyRuleAddRequest.getReplyType();

        // 当为菜单类型时
        if (Objects.equals(replyType, WxReplyRuleTypeEnum.EVENT.getValue())) {

            ThrowUtils.throwIf(StringUtils.isBlank(wxReplyRuleAddRequest.getEventKey()),
                    ErrorCode.PARAMS_ERROR, "菜单栏点击事件key不能为空");

            ThrowUtils.throwIf(wxReplyRuleService.lambdaQuery()
                            .eq(WxReplyRule::getEventKey, wxReplyRuleAddRequest.getEventKey())
                            .eq(WxReplyRule::getAppId, wxReplyRuleAddRequest.getAppId())
                            .exists(),
                    ErrorCode.PARAMS_ERROR, "当前key值已被使用");
        }

        ThrowUtils.throwIf(Objects.equals(replyType, WxReplyRuleTypeEnum.KEYWORDS.getValue()) &&
                        ObjectUtils.isEmpty(wxReplyRuleAddRequest.getMatchValue()),
                ErrorCode.PARAMS_ERROR, "关键字不能为空");

        WxReplyRule wxReplyRule = wxReplyRuleAddRequest.toWxReplyRule();
        wxReplyRule.setUserId(userService.getLoginUser(request).getId());
        wxReplyRuleService.save(wxReplyRule);
        return ResultUtils.success(wxReplyRule.getId());

    }

    /**
     * 更新
     */
    @PostMapping("/update")
    @Operation(summary = "更新回复规则")
    public BaseResponse<Boolean> updateWxReplyRule(@RequestBody WxReplyRuleUpdateRequest wxReplyRuleUpdateRequest) {
        if (ObjectUtils.isEmpty(wxReplyRuleUpdateRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否要修改菜单栏点击事件的key
        WxReplyRule wxReplyRuleDb = wxReplyRuleService.getById(wxReplyRuleUpdateRequest.getId());
        if (ObjectUtil.notEqual(wxReplyRuleDb.getEventKey(), wxReplyRuleUpdateRequest.getEventKey())) {

            // 判断是否已经被使用
            String appId = wxReplyRuleUpdateRequest.getAppId();
            ThrowUtils.throwIf(wxReplyRuleService.count(
                            Wrappers.lambdaQuery(WxReplyRule.class)
                                    .eq(WxReplyRule::getEventKey, wxReplyRuleUpdateRequest.getEventKey())
                                    .eq(WxReplyRule::getAppId, StringUtils.isBlank(appId) ? wxReplyRuleDb.getAppId() : appId)
                    ) > 0,
                    ErrorCode.PARAMS_ERROR,
                    String.format("当前key值已被使用，请更换其他key值，当前key值：%s", wxReplyRuleDb.getEventKey())
            );
        }

        return ResultUtils.success(wxReplyRuleService.updateById(wxReplyRuleUpdateRequest.toWxReplyRule()));

    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @Operation(summary = "根据id删除列表")
    public BaseResponse<Boolean> deleteWxReplyRuleByIds(@RequestBody List<Long> ids) {
        return ResultUtils.success(wxReplyRuleService.removeBatchByIds(ids));
    }

}
