package com.example.blogdemo.shiro;

import cn.hutool.core.bean.BeanUtil;
import com.example.blogdemo.entity.User;
import com.example.blogdemo.service.UserService;
import com.example.blogdemo.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 在重写的shiro类中的relam类
 * shiro进行登录或者权限校验的逻辑
 */
@Component
public class AccountRealm extends AuthorizingRealm {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserService userService;

    /**
     * 判断token是否是Jwt的Token
     * @param token
     * @return
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 权限验证
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    /**
     * 身份验证, 最后执行
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        JwtToken jwtToken = (JwtToken) token; // 在登录部分获取到token, 用于第二次登录认证时获取之前的token值
        System.out.println("------"+token+"------");

        // 获取用户id, 通过工具类进行获取
        // jwtToken.getPrincipal()是 jwt获取token数据中"载荷", 即主体部分
        // 外面的 jwtUtil.getClaimByToken 是校验token的自定义方法类
        // 最后对校验完成的 claim类型数据使用 getsubject返回字符串
        // 这样得到的字符串就是主键
        // 我也不知道为什么
        String userid = jwtUtil.getClaimByToken(String.valueOf(jwtToken.getPrincipal())).getSubject();
//        Claims userclaim = jwtUtil.getClaimByToken(String.valueOf(jwtToken.getPrincipal()));

        User user = userService.getById(userid); // 获取到对应实体

        if (user == null){
            // 为空抛出异常, 异常名未知账号
            // 不同的异常名称只是名称不一样, 同素异形体
            // UnknownAccountException 上溯是运行时异常, 依次向下封装
            throw new UnknownAccountException("账户不存在");
        }

        if (user.getStatus() == -1){
            throw new LockedAccountException("账号锁定");
        }

        AccountRec accountRec = new AccountRec(); // 新建返回数据类
        BeanUtil.copyProperties(user,accountRec); // 通过方法类把两个类中共同的部分赋值

        /*
         出现异常时抛出固定格式
         未出现异常返回另外一个固定格式, 即AccountRec
        */


        return new SimpleAuthenticationInfo(accountRec,jwtToken.getCredentials(),getName());
    }

}
