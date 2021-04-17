package com.example.im.handler;

import org.tio.core.ChannelContext;
import org.tio.websocket.common.WsRequest;

//消息处理器
public interface MsgHandler {
    void handler(String data, WsRequest wsRequest, ChannelContext channelContext);
}
