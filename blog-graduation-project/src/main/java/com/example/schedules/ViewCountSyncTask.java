package com.example.schedules;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.entity.Post;
import com.example.service.PostService;
import com.example.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 把缓存中的阅读量定期同步到数据库中
 */
@Component
public class ViewCountSyncTask {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    PostService postService;


    @Scheduled(cron = "0/5 * * * * *") //每5秒钟同步
    public void task() {

        //获取集合中所有的key
        Set<String> keys = redisTemplate.keys("rank:post:*");

        List<String> ids = new ArrayList<>();
        for (String key : keys) {
            //把有关阅读量的key中的值放进一个集合中
            if(redisUtil.hHasKey(key, "post:viewCount")){
                ids.add(key.substring("rank:post:".length()));
            }
        }

        if(ids.isEmpty()) return;

        // 需要更新阅读量的所有博客
        List<Post> posts = postService.list(new QueryWrapper<Post>().in("id", ids));

        posts.stream().forEach((post) ->{
            Integer viewCount = (Integer) redisUtil.hget("rank:post:" + post.getId(), "post:viewCount");
            redisUtil.hset("rank:post:" + post.getId(), "post:commentCount", post.getCommentCount());
            redisUtil.hset("rank:post:" + post.getId(), "post:id", post.getId());
            redisUtil.hset("rank:post:" + post.getId(), "post:title", post.getTitle());
            post.setViewCount(viewCount);
        });

        if(posts.isEmpty()) return;

        //数据库批量更新博客阅读量
        boolean isSucc = postService.updateBatchById(posts);

        //更新完到数据库之后，删除Redis的阅读量缓存
        if(isSucc) {
            ids.stream().forEach((id) -> {
                redisUtil.hdel("rank:post:" + id, "post:viewCount");
                System.out.println("用户id:" + id + " 的Redis数据---------------------->同步成功");
            });
        }
    }

}
