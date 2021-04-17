package com.example.im.message;

import com.example.im.vo.ImMess;
import lombok.Data;

/**
 * 监听接收的消息
 */
@Data
public class ChatOutMess {

    /**
     * 服务端名
     */
    private String emit;

    /**
     * 消息的来源
     */
    private ImMess data;

}
