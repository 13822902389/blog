package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.Post;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.vo.PostVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 公众号：java思维导图
 * @since 2019-11-17
 */
public interface PostService extends IService<Post> {

    //展示信息通用类：1分页信息 2分类 3用户 4置顶  5精选 6排序
    IPage paging(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order);

    //查询某个博客详情
    PostVo selectOnePost(QueryWrapper<Post> wrapper);

    //本周博客热议，缓存存进Redis
    void initWeekRank();

    //当某个评论数增加1，并集要重新计算一次评论数
    void incrCommentCountAndUnionForWeekRank(long postId, boolean isIncr);

    //文章阅读量
    void putViewCount(PostVo vo);
}
