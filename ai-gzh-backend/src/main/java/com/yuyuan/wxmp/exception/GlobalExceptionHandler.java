package com.yuyuan.wxmp.exception;


import com.yuyuan.wxmp.common.BaseResponse;
import com.yuyuan.wxmp.common.ErrorCode;
import com.yuyuan.wxmp.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;
import java.util.Optional;

/**
 * 全局异常处理器
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }

    /**
     * 微信错误异常处理程序（由于继承的是 Exception， 所以上面那个异常拦截没生效）
     *
     * @param e e
     * @return {@link BaseResponse}<{@link ?}>
     */
    @ExceptionHandler(WxErrorException.class)
    public BaseResponse<?> wxErrorExceptionHandler(WxErrorException e) {
        log.error("WxErrorException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage());
    }


    /**
     * 请求体校验异常处理程序
     *
     * @param e e
     * @return {@link BaseResponse }<{@link ? }>
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public BaseResponse<?> validationExceptionHandler(HandlerMethodValidationException e) {
        String errorMessage = Optional.ofNullable(e)
                .map(HandlerMethodValidationException::getValueResults)
                .map(list -> list.get(0))
                .map(ParameterValidationResult::getResolvableErrors)
                .map(list -> list.get(0))
                .map(MessageSourceResolvable::getDefaultMessage)
                .orElse(ErrorCode.PARAMS_ERROR.getMessage());
        log.error("request body validation failed：{}", errorMessage);
        return ResultUtils.error(ErrorCode.PARAMS_ERROR, errorMessage);
    }

    /**
     * 请求参数校验异常处理程序
     *
     * @param bindException bindException
     * @return {@link BaseResponse }<{@link ? }>
     */
    @ExceptionHandler(BindException.class)
    public BaseResponse<?> validationExceptionHandler(BindException bindException) {
        String errorMessage = Optional.ofNullable(bindException)
                .map(BindException::getBindingResult)
                .map(BindingResult::getAllErrors)
                .map(list -> list.get(0))
                .map(MessageSourceResolvable::getDefaultMessage)
                .orElse(ErrorCode.PARAMS_ERROR.getMessage());
        log.error("parameter validation failed：{}", errorMessage);
        return ResultUtils.error(ErrorCode.PARAMS_ERROR, errorMessage);
    }
}
