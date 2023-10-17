package com.example.blogdemo.controller;


import com.example.blogdemo.common.lang.Result;
import com.example.blogdemo.entity.User;
import com.example.blogdemo.service.UserService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zlx
 * @since 2023-09-07
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService service;

    // 测试登录没有验证
    @RequiresAuthentication //提示要求验证后才能调用该方法
    @GetMapping("/{id}")
    public Object test(@PathVariable("id") Long id){
        return service.getById(id);
    }

    // 测试是否成功链接
    @GetMapping("/test")
    public Object test1(){
        return service.getById(1);
    }

    // 测试数据校验
    @PostMapping("/save")
    public Result save(@Validated @RequestBody User user){
        return Result.succ(user);
    }


}
