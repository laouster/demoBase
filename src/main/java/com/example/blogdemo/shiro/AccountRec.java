package com.example.blogdemo.shiro;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录成功后的返回信息
 * 返回部分的用户信息
 * 实现的接口表明其是用于数据返回
 * @实体类
 */
@Data
public class AccountRec implements Serializable {
    private Long id;
    private String username;
    private String avatar;
    private String email;
}
