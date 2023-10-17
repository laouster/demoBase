package com.example.blogdemo.controller;


import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blogdemo.common.dto.LoginDto;
import com.example.blogdemo.common.lang.Result;
import com.example.blogdemo.entity.User;
import com.example.blogdemo.service.UserService;
import com.example.blogdemo.util.JwtUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 登录接口开发
 */
@RestController
public class AccountController {

    @Autowired
    UserService userService;

    @Autowired
    JwtUtil jwtUtils;

    /**
     * 登录事件, 用户密码在数据库, MarkerHub+111111
     * @param dto
     * @param response
     * @return
     */
    @PostMapping("/login")
    public Result login(@Validated @RequestBody LoginDto dto, HttpServletResponse response){

        User user = userService.getOne(new QueryWrapper<User>().eq("username",dto.getUsername()));
        /*
        assert 是 Java 中的一种断言机制。它是一种用于在程序运行时进行条件检查和验证的特殊语句。
        实际上是一种判断方法
         */
        Assert.notNull(user ,"用户不存在"); // Assert表示断言, 如果user为空, 返回if (object == null) {throw new IllegalArgumentException(message);}
        // 该输出的用户不存在, 为异常信息, 在异常捕获类中捕获进行处理

        if (!user.getPassword() .equals(SecureUtil.md5(dto.getPassword()))){
            // 判断密码是否符合md5加密原则, 不符合抛出异常
            // 防止可能的安全问题
            // 不是对密码加密
            /*
            代码段判断了用户输入的密码经过 MD5 加密后是否与数据库中存储的密码相匹配。
            这种做法可以增加密码的安全性，因为将用户输入的密码与存储的加密密码进行比较，而不是直接比较明文密码。
             */
            return Result.fail("密码错误");
        }

        /**
         * 登录成功生成jwt
         */
        String jwt = jwtUtils.generateToken(user.getId());

        //把jwt注入请求, 按照标签注入
        // response.setHeader表示设置响应头中对应字段的属性, (字段名, 属性值)
        response.setHeader("Authorization",jwt);  //字段Authorization
        response.setHeader("Access-Control-Expose-Headers", "Authorization"); // 字段Access-Control-Expose-Headers
        // 指针的指针结构???

        // 登录成功传递的信息
        return Result.succ(MapUtil.builder()
                .put("id", user.getId())
                .put("username", user.getUsername())
                .put("avatar", user.getAvatar())
                .put("email", user.getEmail())
                .map()
        );
    }

    /**
     * 退出界面
     */
    @GetMapping("/logout")
    @RequiresAuthentication // 赋予登出的权限
    public Result logout() {
        SecurityUtils.getSubject().logout();
        return Result.succ(null);
    }

}
