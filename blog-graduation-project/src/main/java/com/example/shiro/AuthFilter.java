package com.example.shiro;

import cn.hutool.json.JSONUtil;
import com.example.common.lang.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.authc.UserFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * shiro过滤器，判断请求是否是ajax请求
 */
public class AuthFilter extends UserFilter {

    /**
     * 重写重定向到登录页
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        // ajax 弹窗显示未登录 （X-Requested-With 的请求头）
        String header = httpServletRequest.getHeader("X-Requested-With");
        //判断是否为ajax请求
        if(header != null  && "XMLHttpRequest".equals(header)) {
            //判断当前用户是否登录
            boolean authenticated = SecurityUtils.getSubject().isAuthenticated();
            if(!authenticated) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().print(JSONUtil.toJsonStr(Result.fail("请您先登录哦！")));
            }
        } else {
            // web 重定向到登录页面
            super.redirectToLogin(request, response);
        }
    }
}
