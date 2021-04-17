package com.example.vo;

import com.example.entity.UserMessage;
import lombok.Data;

@Data
public class UserMessageVo extends UserMessage {

    /**
     * 回复谁
     */
    private String toUserName;

    /**
     * 来自谁的评论
     */
    private String fromUserName;

    /**
     * 文章标题
     */
    private String postTitle;

    /**
     * 评论内容
     */
    private String commentContent;

}
