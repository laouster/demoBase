package com.example.blogdemo.common.exception;

import com.example.blogdemo.common.lang.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

/**
 * java中的异常处理
 * 全局抓取异常
 * 这样在遇到异常抛出时不会立刻终止程序
 * 只有异常一种没有被捕获并处理时, 才会造成服务器终止
 */
@Slf4j  // Slf4j表示打印日志
@RestControllerAdvice // RestControllerAdvice注解表示异步获取, 相当于使用了该注解的类在项目运行的整个过程中全部执行, 主要用于全局异常处理、全局数据绑定
public class GlobalExceptionHandler {
    // 自定义的类名不能与引用库的类名相同


    /*
    @ResponseStatus 抓取浏览器的状态码
    401对应状态码 UNAUTHORIZED, 就是用户未许可
    200是成功
    400是请求不成功
     */
    // 登录失败异常处理, 返回401
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ShiroException.class)  // 抓取对应异常的注解, 表示在程序执行中throw的对应异常被获取
    public Result handle401(ShiroException e){
        log.error("登录不成功");
        return Result.fail("401",e.getMessage());
    }

    /*
   两个注解都是限定条件, 限定方法
   只有当前端返回的http状态码是BAD_REQUEST
   而后端抛出的异常是RuntimeException时
   才执行方法
   避免错误处理异常
    */
    @ResponseStatus(HttpStatus.BAD_REQUEST)  //指定前端返回的状态码信息
    @ExceptionHandler(value = RuntimeException.class) // 抓取对应异常的注解, 表示在程序执行中throw的对应异常被获取
    public Result handler(RuntimeException e){
        log.error("运行时异常Runtime",e);
        return Result.fail(e.getMessage()); //返回异常信息, 是一个Result变量
    }

    //处理Assert的异常, 即断言异常, 如果密码错误实际上不会产生该异常, 只有Assert出现错误时才会, 比如跨域
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class) // 抓取对应的断言类型抛出的异常, 该异常自带message部分[在断言方法中定义]
    public Result handler(IllegalArgumentException e) throws IOException{
        log.error("Assert断言异常---",e.getMessage());
        return Result.fail(e.getMessage());
    }

    // user实体类中的校验错误异常处理
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result handler(MethodArgumentNotValidException e) throws IOException{
        log.error("校验错误异常",e);
        BindingResult bindingResult = e.getBindingResult();
        ObjectError objectError = bindingResult.getAllErrors().stream().findFirst().get();
        return Result.fail(objectError.getDefaultMessage());
    }




}
