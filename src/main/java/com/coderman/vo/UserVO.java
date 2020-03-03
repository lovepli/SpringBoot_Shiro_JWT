package com.coderman.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author zhangyukang
 * @Date 2020/3/1 16:10
 * @Version 1.0
 **/
@Data
public class UserVO {
    @NotNull(message = "用户名不为空")
    private String username;
    @NotNull(message = "密码不为空")
    private String password;
}
