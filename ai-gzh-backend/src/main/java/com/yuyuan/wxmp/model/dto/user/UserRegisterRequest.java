package com.yuyuan.wxmp.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class UserRegisterRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 3191241716373120793L;

    @NotBlank(message = "用户账号不能为空")
    @Length(min = 4, max = 16, message = "用户账号长度不合法")
    private String userAccount;

    @NotBlank(message = "用户密码不能为空")
    @Length(min = 8, max = 128, message = "用户密码长度不合法")
    private String userPassword;

    @NotBlank(message = "用户确认密码不能为空")
    @Length(min = 8, max = 128, message = "用户确认密码长度不合法")
    private String checkPassword;
}
