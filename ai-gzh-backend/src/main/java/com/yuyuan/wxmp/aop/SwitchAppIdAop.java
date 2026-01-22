package com.yuyuan.wxmp.aop;

import com.yuyuan.wxmp.service.WxAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class SwitchAppIdAop {

    private final WxMpService wxMpService;

    private final WxAccountService wxAccountService;

    @Around("execution(* com.yuyuan..wxmp.controller..*.*(..))")
    public Object extractAppId(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        String appId = null;
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];
            PathVariable pathVariable = AnnotationUtils.findAnnotation(parameter, PathVariable.class);
            if (pathVariable != null) {
                String variableName = pathVariable.value().isEmpty() ? parameter.getName() : pathVariable.value();
                if ("appId".equalsIgnoreCase(variableName)) {
                    appId = (String) args[i];
                    log.info("当前使用的 appId：{}", appId);
                    // 切换公众号
                    wxMpService.switchover(appId);
//                    wxMpService.switchover(appId, mpAppId -> wxAccountService.lambdaQuery()
//                            .eq(WxAccount::getAppId, mpAppId)
//                            .oneOpt()
//                            .orElseThrow(() -> new BusinessException(ErrorCode.OPERATION_ERROR, String.format("当前公众号服务【%s】不存在，请稍后再试", mpAppId)))
//                            .toWxMpConfigStorage()
//                    );
                    break;
                }
            }
        }
        Object result = joinPoint.proceed();
        if (ObjectUtils.isNotEmpty(appId)) {
            // 如果存在 appId，则在线程执行完毕后移除配置，避免空间浪费
//            wxMpService.removeConfigStorage(appId);
        }
        return result;
    }
}