package com.example.blogdemo.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * jwt令牌相关
 * 自定义一个字符串即可当做token
 * @实体类
 */
public class JwtToken implements AuthenticationToken {
    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
