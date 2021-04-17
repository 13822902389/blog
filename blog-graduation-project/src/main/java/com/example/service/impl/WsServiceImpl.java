package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.entity.UserMessage;
import com.example.service.UserMessageService;
import com.example.service.WsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class WsServiceImpl implements WsService {

    @Autowired
    UserMessageService messageService;

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @Async //异步执行
    @Override
    public void sendMessCountToUser(Long toUserId) {
        int count = messageService.count(new QueryWrapper<UserMessage>()
                .eq("to_user_id", toUserId)
                .eq("status", "0")
        );

        // websocket通知 (/user/20/messCount)   user 通道 发送内容
        messagingTemplate.convertAndSendToUser(toUserId.toString(), "/messCount", count);
    }
}
