package com.example.im.vo;

import lombok.Data;

import java.util.Date;

/**
 * 消息的来源实体类
 */
@Data
public class ImMess {

    private String username; //消息来源用户名
    private String avatar;//消息来源用户头像
    private String type;//聊天窗口来源类型，从发送消息传递的to里面获取
    private String content;//消息来源内容
    private Long cid;//消息id，可不传，除非要对消息进行一些操作（如撤回）
    private Boolean mine;//是否我发送的消息，如果为true，则显示在右方
    private Long fromid;//消息的发送者id，（比如群组中的某个消息发送者）
    private Date timestamp;//发送的时间戳
    private Long id;//消息的来源ID（如果是私聊，则是用户id，如果是群聊，则是群组id
}
