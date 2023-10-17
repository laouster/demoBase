package com.example.blogdemo.util;

import com.example.blogdemo.shiro.AccountRec;
import org.apache.shiro.SecurityUtils;

/**
 * 相关工具类
 */
public class ShiroUtil {
    // 获取登录成功后的返回类
    public static AccountRec getRec(){
        return (AccountRec) SecurityUtils.getSubject().getPrincipal();
    }
}
