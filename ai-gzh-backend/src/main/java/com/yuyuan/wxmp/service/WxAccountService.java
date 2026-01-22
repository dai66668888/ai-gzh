package com.yuyuan.wxmp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuyuan.wxmp.model.dto.wxmpaccount.WxAccountAddDTO;
import com.yuyuan.wxmp.model.dto.wxmpaccount.WxAccountPageQueryDTO;
import com.yuyuan.wxmp.model.dto.wxmpaccount.WxAccountUpdateDTO;
import com.yuyuan.wxmp.model.entity.WxAccount;
import com.yuyuan.wxmp.model.vo.WxAccountVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author cq
* @description 针对表【wx_account(微信公众号账号)】的数据库操作Service
* @createDate 2025-03-12 17:30:34
*/
public interface WxAccountService extends IService<WxAccount> {
    /**
     * 获取分页结果
     *
     * @param wxAccountPage wxAccountPage 分页对象
     * @param wxAccountQueryWrapper 微信公众号查询包装器
     * @return {@link Page}<{@link WxAccountVO}>
     */
    Page<WxAccountVO> getPage(Page<WxAccount> wxAccountPage, QueryWrapper<WxAccount> wxAccountQueryWrapper);

    /**
     * 获取查询包装器
     *
     * @param wxAccountPageQueryDTO 微信公众号 DTO
     * @return {@link QueryWrapper}<{@link WxAccount}>
     */
    QueryWrapper<WxAccount> getQueryWrapper(WxAccountPageQueryDTO wxAccountPageQueryDTO);

    /**
     * 保存到数据库 && 保存到wxJava
     *
     * @param wxAccountAddDTO 微信公众号添加请求
     * @param userId          用户 ID
     * @return {@link Long}
     */
    @Transactional(rollbackFor = Throwable.class)
    Long saveAndToRuntime(WxAccountAddDTO wxAccountAddDTO, Long userId);

    @Transactional(rollbackFor = Throwable.class)
    Boolean updateAndToRuntime(WxAccountUpdateDTO wxAccountUpdateDTO);

    @Transactional(rollbackFor = Throwable.class)
    Boolean deleteByAppIds(List<String> appIds);

}
