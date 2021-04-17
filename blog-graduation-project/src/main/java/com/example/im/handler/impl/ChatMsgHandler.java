package com.example.im.handler.impl;

import cn.hutool.json.JSONUtil;
import com.example.common.lang.Consts;
import com.example.im.handler.MsgHandler;
import com.example.im.handler.filter.ExculdeMineChannelContextFilter;
import com.example.im.message.ChatImMess;
import com.example.im.message.ChatOutMess;
import com.example.im.vo.ImMess;
import com.example.im.vo.ImTo;
import com.example.im.vo.ImUser;
import com.example.service.ChatService;
import com.example.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.common.WsResponse;

import javax.annotation.Resource;
import java.util.Date;

//群聊信息处理器
@Slf4j
public class ChatMsgHandler implements MsgHandler {

    @Override
    public void handler(String data, WsRequest wsRequest, ChannelContext channelContext) {
        ChatImMess chatImMess = JSONUtil.toBean(data, ChatImMess.class);

        ImUser mine = chatImMess.getMine();
        ImTo to = chatImMess.getTo();

        // 处理消息
        ImMess imMess = new ImMess();
        imMess.setContent(mine.getContent());
        imMess.setAvatar(mine.getAvatar());
        imMess.setMine(false); // 是否是我发送的消息

        imMess.setUsername(mine.getUsername());
        imMess.setFromid(mine.getId());

        imMess.setId(Consts.IM_GROUP_ID);
        imMess.setTimestamp(new Date());
        imMess.setType(to.getType());


        ChatOutMess chatOutMess = new ChatOutMess();
        chatOutMess.setEmit("chatMessage");
        chatOutMess.setData(imMess);

        String result = JSONUtil.toJsonStr(chatOutMess);
        log.info("群聊消息----------> {}", result);

        WsResponse wsResponse = WsResponse.fromText(result, "utf-8");

        ExculdeMineChannelContextFilter filter = new ExculdeMineChannelContextFilter();
        filter.setCurrentContext(channelContext);

        //发送给群
        Tio.sendToGroup(channelContext.getGroupContext(), Consts.IM_GROUP_NAME, wsResponse, filter);

        //保存群聊信息
        ChatService chatService = (ChatService) SpringUtil.getBean("chatService");
        chatService.setGroupHistoryMsg(imMess);

    }
}
