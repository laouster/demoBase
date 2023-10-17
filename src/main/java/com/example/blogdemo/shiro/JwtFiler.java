package com.example.blogdemo.shiro;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.blogdemo.common.lang.Result;
import com.example.blogdemo.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 拦截器
 * 在ShiroConfig中定义了所有请求都会经过该拦截器
 */
@Component
public class JwtFiler extends AuthenticatingFilter {

    @Autowired
    JwtUtil jwtUtil;

    /**
     *     从handle中获取到对应字符串, 封装为token
     *     按照其字符串的表头header进行查找对应字符串
     *     有字符串则进行生成token
      */
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String jwt = request.getHeader("Authorization"); // 按照设置的表头获取到字符串, 未获取到就是空

        if (StringUtils.isEmpty(jwt)){ // 如果没有成功获取字符串, 返回null
            return null;
        }

        return new JwtToken(jwt);  //返回JwtToken自定义类的格式的token, 或者说返回对应实例

    }

    /**
     * 校验token是否异常
     * 如token是否过期之类
     * @param servletRequest
     * @param servletResponse
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {

        // 如果没有token, 就直接跳过拦截器
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String jwt = request.getHeader("Authorization"); // 按照设置的表头获取到字符串, 未获取到就是空
        if (StringUtils.isEmpty(jwt)){
            return true;
        }else{

            // 如果有token, 就进行校验, 校验使用JwtUtil类
            Claims claims = jwtUtil.getClaimByToken(jwt); // 使用claim类, 用于校验
            if (claims == null || jwtUtil.isTokenExpired(claims.getExpiration())){
                // 如果调用的claim为空, 或者过期了
                throw new ExpiredCredentialsException("token失效, 重新登陆");
//                不断token失效?

            }

            // 如果没有出错, 执行登录
            return executeLogin(servletRequest, servletResponse); // 执行登录操作
            /*
            登录操作在之后执行
             */
        }
    }

    /**
     * 登录失败/异常 的返回值, 返回错误信息 r,
     *  把json数据返回到前端
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletResponse httpResponse = (HttpServletResponse) response; // 返回控制类
        try {
            //处理登录失败的异常
            Throwable throwable = e.getCause() == null ? e : e.getCause();
            Result r = Result.fail(throwable.getMessage());
            String json = JSONUtil.toJsonStr(r);
            httpResponse.getWriter().print(json);
        } catch (IOException e1) {
        }
        return false;
    }


    /**
     * 在拦截器类中设置跨域相关
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(org.springframework.http.HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
}
