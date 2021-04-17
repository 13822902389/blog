package com.example.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.search.mq.PostMqIndexMessage;
import com.example.vo.PostVo;

import java.util.List;

public interface SearchService {

    /**
     * 关键字搜索并分页
     * @param page
     * @param keyword
     * @return
     */
    IPage search(Page page, String keyword);

    /**
     * 初始化ES的数据（将mysql的post表的所有数据存进ES）分页同步进ES
     * @param records
     * @return
     */
    int initEsData(List<PostVo> records);

    /**
     * 添加或者更新ES索引
     * @param message
     */
    void createOrUpdateIndex(PostMqIndexMessage message);

    /**
     * 删除ES索引
     * @param message
     */
    void removeIndex(PostMqIndexMessage message);
}
