package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * websocket配置
 */
@EnableAsync
@Configuration
@EnableWebSocketMessageBroker // 表示开启使用STOMP协议来传输基于代理的消息
public class WsConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 注册stomp端点
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket") // 注册一个端点，websocket的访问地址
                .withSockJS(); // 表示一些降级的技术，比如有些浏览器不支持，会降级使用其他的方法

    }

    /**
     * 配置消息代理点
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/user/", "/topic/"); //推送消息前缀
        registry.setApplicationDestinationPrefixes("/app");//用app开头进入websocket
    }
}
