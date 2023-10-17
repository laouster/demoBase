package com.example.blogdemo.config;

import com.example.blogdemo.shiro.AccountRealm;
import com.example.blogdemo.shiro.JwtFiler;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 重写shiro相关配置
 * 因为不同数据库的名称不同, 需要对引入的外部库进行重写
 * @配置
 */
@Configuration
public class ShiroConfig {

    @Autowired
    JwtFiler jwtFiler;

    @Bean
    public SessionManager sessionManager(RedisSessionDAO redisSessionDAO) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();

        // inject redisSessionDAO
        sessionManager.setSessionDAO(redisSessionDAO);

        // other stuff...

        return sessionManager;
    }

    @Bean
    public SessionsSecurityManager securityManager(AccountRealm realms, SessionManager sessionManager, RedisCacheManager redisCacheManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(realms);

        //inject sessionManager
        securityManager.setSessionManager(sessionManager);

        // inject redisCacheManager
        securityManager.setCacheManager(redisCacheManager);

        // other stuff...

        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);

        return securityManager;
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/**", " authc"); // 主要通过注解方式校验权限, 这里的authc是配置的过滤器名称, 前面的/**是路径, 相当于所有的路径都有通过该过滤器
        /*  巨大报错解决
        原因未知, 在把jwt过滤器改为 authc 过滤器后导致报错 Could not get a resource from the pool
        过滤器不同导致不能成功获取到数据库池????

        原因是这里的filterMap.put("/**", " authc"); 需要与后面的filters.put("authc", jwtFiler);对应

        更深层次的原因是 Could not get a resource from the pool 表明redis服务的数据库没有找到连接池, 就是没有找到链接
        在filterMap.put 和 filters.put 没有对应上时, 导致服务器定义的拦截器和实际上的拦截器不一致
        filterMap.put("/**", "authc") 和 filters.put("authc", jwtFilter) 是 Apache Shiro 中配置过滤器链的两个部分，它们之间存在一定的关联。

        filterMap.put("/**", "authc")：
        这行代码表示将所有访问路径（"/**"）都应用于 authc 过滤器。filterMap 是用于配置过滤器链的一个映射表，键表示请求路径模式，值表示要应用的过滤器名称。在这里，"authc" 表示应用身份验证过滤器。

        filters.put("authc", jwtFilter)：
        这行代码将一个名为 "authc" 的过滤器与 jwtFilter 对象进行关联。filters 是用于配置过滤器的映射表，键表示过滤器名称，值表示实际的过滤器对象或实例。在这里，"authc" 表示身份验证过滤器，jwtFilter 则是实际使用的自定义 JWT 过滤器对象。

        通过这两段代码，我们可以看到 "authc" 过滤器的配置和关联情况。当请求访问任意路径时，会经过 filterMap 的配置，进而触发名为 "authc" 的过滤器。

        通常情况下，filterMap.put("/**", "authc") 配置的过滤器是 Shiro 提供的默认身份验证过滤器，用于实现基于表单或基于 HTTP Basic 的身份验证。而 filters.put("authc", jwtFilter) 则是自定义的 JWT 过滤器，用于实现基于 JSON Web Token 的身份验证方式。
         */
        chainDefinition.addPathDefinitions(filterMap);
        return chainDefinition;
    }
    @Bean("shiroFilterFactoryBean")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager,
                                                         ShiroFilterChainDefinition shiroFilterChainDefinition) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);

        Map<String, Filter> filters = new HashMap<>(); //导入的是selevt的Filter
//        filters.put("jwt", jwtFiler);
        filters.put("authc", jwtFiler);

        shiroFilter.setFilters(filters);

        Map<String, String> filterMap = shiroFilterChainDefinition.getFilterChainMap();
        shiroFilter.setFilterChainDefinitionMap(filterMap);
        return shiroFilter;
    }

}
