package com.yuyuan.wxmp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.yuyuan.wxmp.common.ErrorCode;
import com.yuyuan.wxmp.common.PageRequest;
import com.yuyuan.wxmp.constant.MqConstant;
import com.yuyuan.wxmp.exception.BusinessException;
import com.yuyuan.wxmp.exception.ThrowUtils;
import com.yuyuan.wxmp.mapper.WxAccountMapper;
import com.yuyuan.wxmp.model.dto.wxmpaccount.WxAccountAddDTO;
import com.yuyuan.wxmp.model.dto.wxmpaccount.WxAccountPageQueryDTO;
import com.yuyuan.wxmp.model.dto.wxmpaccount.WxAccountUpdateDTO;
import com.yuyuan.wxmp.model.entity.User;
import com.yuyuan.wxmp.model.entity.WxAccount;
import com.yuyuan.wxmp.model.vo.WxAccountVO;
import com.yuyuan.wxmp.service.UserService;
import com.yuyuan.wxmp.service.WxAccountService;
import com.yuyuan.wxmp.utils.CopyUtil;
import com.yuyuan.wxmp.utils.WrapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author cq
 * @description 针对表【wx_account(微信公众号账号)】的数据库操作Service实现
 * @createDate 2025-03-12 17:30:34
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WxAccountServiceImpl extends ServiceImpl<WxAccountMapper, WxAccount>
        implements WxAccountService {

    private final WxMpService wxMpService;
    private final UserService userService;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public Page<WxAccountVO> getPage(Page<WxAccount> wxAccountPage, QueryWrapper<WxAccount> wxAccountQueryWrapper) {
        Page<WxAccount> accountPage = this.page(wxAccountPage, wxAccountQueryWrapper);
        Page<WxAccountVO> pageResult = new Page<>();
        BeanUtils.copyProperties(accountPage, pageResult, "records");

        List<WxAccount> accountList = accountPage.getRecords();

        Set<Long> userIdSet = accountList.stream().map(WxAccount::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap;
        if (ObjectUtils.isNotEmpty(userIdSet)) {
            userIdUserListMap = userService.listByIds(userIdSet)
                    .stream()
                    .collect(Collectors.groupingBy(User::getId));
        } else {
            userIdUserListMap = new HashMap<>();
        }

        // 转换为VO
        pageResult.setRecords(
                accountList
                        .stream()
                        .map(wxAccount -> {
                            WxAccountVO wxAccountVO = CopyUtil.copy(wxAccount, WxAccountVO.class);
                            List<User> userList = userIdUserListMap.get(wxAccount.getUserId());
                            if (ObjectUtils.isNotEmpty(userList)) {
                            wxAccountVO.setUser(userService.getUserVO(userList.get(0)));
                        }
                            return wxAccountVO;
                        })
                        .collect(Collectors.toList())
        );

        return pageResult;
    }


    @Override
    public Long saveAndToRuntime(WxAccountAddDTO wxAccountAddDTO, Long userId) {
        ThrowUtils.throwIf(
                this.count(Wrappers.lambdaQuery(WxAccount.class).eq(WxAccount::getAppId, wxAccountAddDTO.getAppId())) != 0,
                ErrorCode.PARAMS_ERROR,
                "公众号已存在"
        );
        String appId = wxAccountAddDTO.getAppId();
        synchronized (appId.intern()) {
            if (this.isAccountInRuntime(appId)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "公众号已存在");
            }
            WxAccount wxAccount = CopyUtil.copy(wxAccountAddDTO, WxAccount.class);
            wxAccount.setUserId(userId);
            // 保存
            this.save(wxAccount);

            // 添加到wxJava
            this.addAccountToRuntime(wxAccount);
            return wxAccount.getId();
        }
    }

    @Override
    public Boolean updateAndToRuntime(WxAccountUpdateDTO wxAccountUpdateDTO) {
        WxAccount wxAccountDb = this.getById(wxAccountUpdateDTO.getId());
        ThrowUtils.throwIf(ObjectUtils.isEmpty(wxAccountDb), ErrorCode.PARAMS_ERROR, "公众号不存在");
        String oldAppId = wxAccountDb.getAppId();
        if (!this.isAccountInRuntime(oldAppId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "公众号不存在");
        }

        // 如果 appId 为空，则使用原来的 appId
        if (StringUtils.isBlank(wxAccountUpdateDTO.getAppId())) {
            wxAccountUpdateDTO.setAppId(oldAppId);
        }
        WxAccount wxAccount = CopyUtil.copy(wxAccountUpdateDTO, WxAccount.class);

        // 更新
        boolean result = SqlHelper.retBool(this.baseMapper.updateById(wxAccount));

        // 先移除
        wxMpService.removeConfigStorage(oldAppId);
        // 删除后再填加到 wxJava（这里再查一遍目的是拿到最新的微信公众号信息，后面如果又加了字段，不这样做可能会漏掉字段）
        this.addAccountToRuntime(this.getById(wxAccountUpdateDTO.getId()));

        return result;
    }

    @Override
    public Boolean deleteByAppIds(List<String> appIds) {
        // 先删除wxJava里的数据
        rabbitTemplate.convertAndSend(MqConstant.REMOVE_WXMP_CONFIG_EXCHANGE,
                "", appIds);
//        appIds.forEach(wxMpService::removeConfigStorage);

        // 再删除数据库里的数据
        return this.remove(
                Wrappers.lambdaQuery(WxAccount.class)
                        .in(WxAccount::getAppId, appIds)
        );

    }


    @Override
    public QueryWrapper<WxAccount> getQueryWrapper(WxAccountPageQueryDTO wxAccountPageQueryDTO) {
        if (wxAccountPageQueryDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String appId = wxAccountPageQueryDTO.getAppId();
        String name = wxAccountPageQueryDTO.getName();
        Boolean verified = wxAccountPageQueryDTO.getVerified();
        String secret = wxAccountPageQueryDTO.getSecret();
        String token = wxAccountPageQueryDTO.getToken();
        String aesKey = wxAccountPageQueryDTO.getAesKey();
        String sortField = wxAccountPageQueryDTO.getSortField();
        String sortOrder = wxAccountPageQueryDTO.getSortOrder();
        List<PageRequest.Sorter> sorterList = wxAccountPageQueryDTO.getSorterList();
        QueryWrapper<WxAccount> queryWrapper = Wrappers.query();
        queryWrapper.eq(StringUtils.isNotBlank(appId), "appId", appId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(verified), "verified", verified);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(secret), "secret", secret);
        queryWrapper.like(StringUtils.isNotBlank(token), "userName", token);
        queryWrapper.like(StringUtils.isNotBlank(aesKey), "aesKey", aesKey);
        WrapperUtil.handleOrder(queryWrapper, sorterList, sortField, sortOrder);
        return queryWrapper;
    }

    private synchronized void addAccountToRuntime(WxAccount wxAccount) {
        rabbitTemplate.convertAndSend(MqConstant.ADD_WXMP_CONFIG_EXCHANGE,
                "", wxAccount);
//        String appId = wxAccount.getAppId();
//        WxMpDefaultConfigImpl config = wxAccount.toWxMpConfigStorage();
//        try {
//            wxMpService.addConfigStorage(appId, config);
//        } catch (NullPointerException e) {
//            log.info("初始化configStorageMap...");
//            Map<String, WxMpConfigStorage> configStorages = new HashMap<>(6);
//            configStorages.put(appId, config);
//            wxMpService.setMultiConfigStorages(configStorages, appId);
//        }
    }

    private boolean isAccountInRuntime(String appid) {
        try {
            return wxMpService.switchover(appid);
        } catch (NullPointerException e) {
            // sdk bug，未添加任何账号时configStorageMap为null会出错
            return false;
        }
    }
}




