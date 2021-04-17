package com.example.im.message;

import com.example.im.vo.ImTo;
import com.example.im.vo.ImUser;
import lombok.Data;

/**
 * 监听发送的消息
 */
@Data
public class ChatImMess {

    /**
     * 用户个人信息
     */
    private ImUser mine;

    /**
     * 发送给谁
     */
    private ImTo to;

}
