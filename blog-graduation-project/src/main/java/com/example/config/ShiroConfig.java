package com.example.config;

import cn.hutool.core.map.MapUtil;
import com.example.shiro.AccountRealm;
import com.example.shiro.AuthFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * shiro配置
 */
@Slf4j
@Configuration
public class ShiroConfig {

    @Bean
    public SecurityManager securityManager(AccountRealm accountRealm){

        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //关联accountRealm
        securityManager.setRealm(accountRealm);
        log.info("------------------>securityManager注入成功");
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {

        ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
        //设置安全管理器
        filterFactoryBean.setSecurityManager(securityManager);
        // 配置登录的url和登录成功的url
        filterFactoryBean.setLoginUrl("/login");
        filterFactoryBean.setSuccessUrl("/user/center");
        // 配置未授权跳转页面
        filterFactoryBean.setUnauthorizedUrl("/error/403");

        //配置自定义的过滤器 （没登录不能进行下一步操作的过滤器）
        filterFactoryBean.setFilters(MapUtil.of("auth", authFilter()));

        Map<String, String> hashMap = new LinkedHashMap<>();

        /*添加shiro的内置过滤器
        anon:无需认证就能访问
        authc:必须认证了才能访问
        user:必须拥有 记住我 功能才能用
        perms:拥有对某个资源的权限才能访问
        role:拥有某个角色权限才能访问
        */
        hashMap.put("/res/**", "anon");
        hashMap.put("/user/home", "auth");
        hashMap.put("/user/set", "auth");
        hashMap.put("/user/upload", "auth");
        hashMap.put("/user/index", "auth");
        hashMap.put("/user/public", "auth");
        hashMap.put("/user/collection", "auth");
        hashMap.put("/user/mess", "auth");
        hashMap.put("/msg/remove/", "auth");
        hashMap.put("/message/nums/", "auth");
        hashMap.put("/collection/remove/", "auth");
        hashMap.put("/collection/find/", "auth");
        hashMap.put("/collection/add/", "auth");
        hashMap.put("/post/edit", "auth");
        hashMap.put("/post/submit", "auth");
        hashMap.put("/post/delete", "auth");
        hashMap.put("/post/reply/", "auth");
        hashMap.put("/websocket", "anon");
        hashMap.put("/login", "anon");
        filterFactoryBean.setFilterChainDefinitionMap(hashMap);

        return filterFactoryBean;

    }

    @Bean
    public AuthFilter authFilter() {
        return new AuthFilter();
    }
}
