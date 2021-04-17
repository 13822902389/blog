package com.example.search.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * 博客首页搜索的实体类，ES
 */
@Data
@Document(indexName="post", type="post", createIndex=true) //启动时自动在ES创建索引
public class PostDocment implements Serializable {

    @Id
    private Long id;

    // ik分词器
    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String title;

    @Field(type = FieldType.Long)
    private Long authorId;

    @Field(type = FieldType.Keyword)
    private String authorName;
    private String authorAvatar;

    /**
     * 分类id
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    @Field(type = FieldType.Keyword)
    private String categoryName;

    /**
     * 置顶
     */
    private Integer level;

    /**
     * 精华
     */
    private Boolean recomment;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 阅读量
     */
    private Integer viewCount;

    @Field(type = FieldType.Date)
    private Date created;


}
