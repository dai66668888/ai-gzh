package com.yuyuan.wxmp.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class UserLoginRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 3191241716373120793L;

    @NotBlank(message = "用户账号不能为空")
    @Length(min = 4, max = 16, message = "账号或密码错误")
    private String userAccount;

    @NotBlank(message = "用户密码不能为空")
    @Length(min = 8, max = 128, message = "账号或密码错误")
    private String userPassword;
}
