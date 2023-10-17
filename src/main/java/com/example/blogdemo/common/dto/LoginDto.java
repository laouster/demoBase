package com.example.blogdemo.common.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录相关
 * 使用DTO设计模式
 * 数据传输对象设计模式
 */
@Data
public class LoginDto {

    @NotBlank(message = "昵称不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
}
