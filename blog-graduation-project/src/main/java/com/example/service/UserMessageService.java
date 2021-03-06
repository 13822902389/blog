package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.UserMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 公众号：java思维导图
 * @since 2019-11-17
 */
public interface UserMessageService extends IService<UserMessage> {

    /**
     * 我的消息分页
     * @param page
     * @param wrapper
     * @return
     */
    IPage paging(Page page, QueryWrapper<UserMessage> wrapper);

    /**
     * 更新已读消息
     * @param ids
     */
    void updateToReaded(List<Long> ids);
}
