package com.yuyuan.wxmp.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuyuan.wxmp.common.BaseResponse;
import com.yuyuan.wxmp.common.ErrorCode;
import com.yuyuan.wxmp.common.ResultUtils;
import com.yuyuan.wxmp.exception.BusinessException;
import com.yuyuan.wxmp.exception.ThrowUtils;
import com.yuyuan.wxmp.model.dto.wxmpaccount.WxAccountAddDTO;
import com.yuyuan.wxmp.model.dto.wxmpaccount.WxAccountPageQueryDTO;
import com.yuyuan.wxmp.model.dto.wxmpaccount.WxAccountUpdateDTO;
import com.yuyuan.wxmp.model.entity.User;
import com.yuyuan.wxmp.model.entity.WxAccount;
import com.yuyuan.wxmp.model.vo.WxAccountVO;
import com.yuyuan.wxmp.service.UserService;
import com.yuyuan.wxmp.service.WxAccountService;
import com.yuyuan.wxmp.utils.CopyUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 公众号账号
 *
 * @author cq
 * @since 2023/11/21
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/wx/account")
public class WxAccountController {

    private final WxAccountService wxAccountService;

    private final UserService userService;


    @GetMapping("/token")
    @Operation(summary = "获取一个随机的32位token")
    public BaseResponse<String> getToken() {
        return ResultUtils.success(DigestUtil.md5Hex(RandomUtil.randomString(16)));
    }

    /**
     * 列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询公众号")
    public BaseResponse<Page<WxAccountVO>> listWxMpAccountByPage(WxAccountPageQueryDTO wxAccountPageQueryDTO) {
        long current = wxAccountPageQueryDTO.getCurrent();
        long size = wxAccountPageQueryDTO.getPageSize();
        return ResultUtils.success(wxAccountService.getPage(new Page<>(current, size),
                wxAccountService.getQueryWrapper(wxAccountPageQueryDTO)));
    }


    /**
     * 信息
     */
    @GetMapping("/get/vo")
    @Operation(summary = "公众号详情")
    public BaseResponse<WxAccountVO> getWxMpVOById(Long id) {
        WxAccount wxAccount = wxAccountService.getById(id);

        WxAccountVO wxAccountVO = CopyUtil.copy(wxAccount, WxAccountVO.class);
        wxAccountVO.setUser(userService.getUserVO(userService.getById(wxAccount.getUserId())));
        return ResultUtils.success(wxAccountVO);
    }

    /**
     * 新增
     */
    @PostMapping("/add")
    @Operation(summary = "新增公众号")
    public BaseResponse<Long> addWxMpAccount(@RequestBody WxAccountAddDTO wxAccountAddDTO, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(wxAccountAddDTO)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ThrowUtils.throwIf(StringUtils.isBlank(wxAccountAddDTO.getAppId()), ErrorCode.PARAMS_ERROR, "appId不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(wxAccountAddDTO.getName()), ErrorCode.PARAMS_ERROR, "公众号名称不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(wxAccountAddDTO.getSecret()), ErrorCode.PARAMS_ERROR, "秘钥不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(wxAccountAddDTO.getToken()), ErrorCode.PARAMS_ERROR, "token不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(wxAccountAddDTO.getAesKey()), ErrorCode.PARAMS_ERROR, "aesKey不能为空");

        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(wxAccountService.saveAndToRuntime(wxAccountAddDTO, loginUser.getId()));

    }

    /**
     * 更新
     */
    @PostMapping("/update")
    @Operation(summary = "更新公众号信息")
    public BaseResponse<Boolean> updateWxMpAccount(@RequestBody WxAccountUpdateDTO wxAccountUpdateDTO) {
        if (ObjectUtils.isEmpty(wxAccountUpdateDTO)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ThrowUtils.throwIf(ObjectUtils.isEmpty(wxAccountUpdateDTO.getId()), ErrorCode.PARAMS_ERROR, "id不能为空");

        return ResultUtils.success(wxAccountService.updateAndToRuntime(wxAccountUpdateDTO));

    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @Operation(summary = "根据appId删除列表，这样后端处理更方便")
    public BaseResponse<Boolean> deleteWxMpAccount(@RequestBody List<String> appIds) {
        return ResultUtils.success(wxAccountService.deleteByAppIds(appIds));
    }

}
