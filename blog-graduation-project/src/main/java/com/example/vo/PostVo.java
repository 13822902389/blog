package com.example.vo;

import com.example.entity.Post;
import lombok.Data;

@Data
public class PostVo extends Post {

    /**
     * 用户id
     */
    private Long authorId;

    /**
     * 用户名字
     */
    private String authorName;

    /**
     * 用户头像
     */
    private String authorAvatar;

    /**
     * 分类名称
     */
    private String categoryName;

}
