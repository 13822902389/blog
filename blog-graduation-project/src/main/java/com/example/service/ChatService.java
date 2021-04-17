package com.example.service;

import com.example.im.vo.ImMess;
import com.example.im.vo.ImUser;
import org.springframework.stereotype.Component;

import java.util.List;

public interface ChatService {
    /**
     * 获取当前群聊用户
     * @return
     */
    ImUser getCurrentUser();

    /**
     * 把群聊历史记录放进Redis缓存中
     * @param responseMess
     */
    void setGroupHistoryMsg(ImMess responseMess);

    /**
     * 从Redis缓存中获取群聊历史记录
     * @param count 20条历史聊天
     * @return
     */
    List<Object> getGroupHistoryMsg(int count);
}
