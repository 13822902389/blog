package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**

 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_user_collection")
public class UserCollection extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 文章id
     */
    private Long postId;

    /**
     * 某个文章的用户id
     */
    private Long postUserId;


}
