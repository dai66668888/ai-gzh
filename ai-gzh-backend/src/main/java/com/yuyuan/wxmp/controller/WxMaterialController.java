package com.yuyuan.wxmp.controller;


import cn.hutool.core.io.FileUtil;
import com.yuyuan.wxmp.common.BaseResponse;
import com.yuyuan.wxmp.common.ErrorCode;
import com.yuyuan.wxmp.common.ResultUtils;
import com.yuyuan.wxmp.exception.BusinessException;
import com.yuyuan.wxmp.exception.ThrowUtils;
import com.yuyuan.wxmp.model.dto.wxmpmaterial.MaterialDeleteRequest;
import com.yuyuan.wxmp.model.dto.wxmpmaterial.WxMaterialQueryRequest;
import com.yuyuan.wxmp.model.entity.User;
import com.yuyuan.wxmp.model.enums.WxMaterialTypeEnum;
import com.yuyuan.wxmp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.material.WxMpMaterial;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialFileBatchGetResult;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialVideoInfoResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 微信公众号素材管理接口
 *
 * @author cq
 * @since 2025/03/11
 */
@RestController
@RequestMapping("/wx/material")
@RequiredArgsConstructor
//@Api(tags = "公众号素材管理")
@Slf4j
public class WxMaterialController {
    private final WxMpService wxMpService;

    private final UserService userService;

    /**
     * 允许上传的图片类型
     */
    private static final List<String> ALLOW_IMG_TYPE = Arrays.asList("bmp", "png", "jpeg", "jpg", "gif");


    /**
     * 允许上传的音频类型
     */
    private static final List<String> ALLOW_VOICE_TYPE = Arrays.asList("mp3", "wma", "wav", "amr");


    /**
     * 允许上传的视频类型
     */
    private static final List<String> ALLOW_VIDEO_TYPE = Collections.singletonList("mp4");


    @GetMapping("/get/type")
    @Operation(summary = "获取素材类型")
    public BaseResponse<WxMaterialTypeEnum[]> getMaterialType() {
        return ResultUtils.success(WxMaterialTypeEnum.values());
    }

    @PostMapping("/{appId}/upload")
    @Operation(summary = "上传素材（图片、音频、视频）")
    public BaseResponse<Boolean> uploadMaterial(@PathVariable String appId, @RequestPart("file") MultipartFile multipartFile, String materialType, HttpServletRequest request) throws WxErrorException, IOException {
        WxMaterialTypeEnum wxMaterialTypeEnum = WxMaterialTypeEnum.getEnumByValue(materialType);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(wxMaterialTypeEnum), ErrorCode.PARAMS_ERROR);

        // 校验文件
        this.validFile(multipartFile, wxMaterialTypeEnum);

        String originalFilename = multipartFile.getOriginalFilename();
        User loginUser = userService.getLoginUser(request);
        String filepath = String.format("%s/%s/%s/%s", System.getProperty("user.dir"), loginUser.getId(), UUID.randomUUID(), originalFilename);
        File file = null;
        try {
            // 上传文件
            file = new File(filepath);
            FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);
            WxMpMaterial wxMpMaterial = new WxMpMaterial();
            wxMpMaterial.setName(originalFilename);
            wxMpMaterial.setFile(file);
            wxMpMaterial.setVideoTitle(originalFilename);
//            wxMpService.switchover(appId);
            wxMpService.getMaterialService().materialFileUpload(materialType, wxMpMaterial);
            return ResultUtils.success(true);
        } catch (Exception e) {
            log.error("file upload error, filepath = {}", filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                FileUtil.del(file);
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    @PostMapping("/{appId}/delete")
    @Operation(summary = "删除素材")
    public BaseResponse<Boolean> deleteMaterial(@PathVariable String appId, @RequestBody MaterialDeleteRequest materialDeleteRequest, HttpServletRequest request) throws WxErrorException, IOException {
        String materialId = materialDeleteRequest.getMaterialId();
//        wxMpService.switchover(appId);
        return ResultUtils.success(wxMpService.getMaterialService().materialDelete(materialId));
    }


    @GetMapping("/{appId}/list")
    @Operation(summary = "查询素材列表")
    public BaseResponse<WxMpMaterialFileBatchGetResult> listAllMaterial(@PathVariable String appId, WxMaterialQueryRequest wxMaterialQueryRequest) throws WxErrorException {
        String materialType = wxMaterialQueryRequest.getMaterialType();
        int current = (int) wxMaterialQueryRequest.getCurrent();
        int pageSize = (int) wxMaterialQueryRequest.getPageSize();
        int offset = (current - 1) * pageSize;

//        wxMpService.switchover(appId);
        return ResultUtils.success(wxMpService.getMaterialService().materialFileBatchGet(materialType, offset, pageSize));
    }

    @GetMapping("/{appId}/video/info")
    @Operation(summary = "查询视频详情")
    public BaseResponse<WxMpMaterialVideoInfoResult> getMaterialVideoByMaterialId(@PathVariable String appId, @RequestParam String materialId) throws WxErrorException {
        ThrowUtils.throwIf(StringUtils.isBlank(materialId), ErrorCode.PARAMS_ERROR);
//        wxMpService.switchover(appId);
        return ResultUtils.success(wxMpService.getMaterialService().materialVideoInfo(materialId));
    }

    @GetMapping("/{appId}/img_voice/download")
    @Operation(summary = "下载图片或音频")
    public ResponseEntity<InputStreamResource> downloadImgAndVoiceMaterial(@PathVariable String appId, String materialId, String fileName) throws WxErrorException {
        ThrowUtils.throwIf(StringUtils.isAnyBlank(materialId, fileName), ErrorCode.PARAMS_ERROR);
//        wxMpService.switchover(appId);
        try (InputStream inputStream = wxMpService.getMaterialService().materialImageOrVoiceDownload(materialId)) {
            InputStreamResource resource = new InputStreamResource(inputStream);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + UriUtils.encode(fileName, StandardCharsets.UTF_8) + "\"")
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void validFile(MultipartFile multipartFile, WxMaterialTypeEnum wxMaterialTypeEnum) {
        String originalFilename = multipartFile.getOriginalFilename();
        long fileSize = multipartFile.getSize();
        String fileSuffix = FileUtil.getSuffix(originalFilename);
        ThrowUtils.throwIf(StringUtils.isBlank(fileSuffix), ErrorCode.PARAMS_ERROR, "未知的文件类型");
        fileSuffix = fileSuffix.toLowerCase();
        final long oneM = 1024 * 1024L;
        switch (wxMaterialTypeEnum) {
            case IMAGE:
                ThrowUtils.throwIf(!ALLOW_IMG_TYPE.contains(fileSuffix), ErrorCode.PARAMS_ERROR);
                ThrowUtils.throwIf(fileSize > 10 * oneM, ErrorCode.PARAMS_ERROR, "图片过大");
                break;
            case VOICE:
                ThrowUtils.throwIf(!ALLOW_VOICE_TYPE.contains(fileSuffix), ErrorCode.PARAMS_ERROR);
                ThrowUtils.throwIf(fileSize > 2 * oneM, ErrorCode.PARAMS_ERROR, "音频过大");
                break;
            case VIDEO:
                ThrowUtils.throwIf(!ALLOW_VIDEO_TYPE.contains(fileSuffix), ErrorCode.PARAMS_ERROR);
                ThrowUtils.throwIf(fileSize > 10 * oneM, ErrorCode.PARAMS_ERROR, "视频过大");
                break;
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "暂未实现该类型的上传");
        }
    }
}
