package com.example.template;

import com.example.common.templates.DirectiveHandler;
import com.example.common.templates.TemplateDirective;
import com.example.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 本周热议模板
 */
@Component
public class HotsTemplate extends TemplateDirective {

    @Autowired
    RedisUtil redisUtil;

    @Override
    public String getName() {
        return "hots";
    }

    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        String weekRankKey = "week:rank";

        //本周热议排行榜 ，展示6条
        Set<ZSetOperations.TypedTuple> typedTuples = redisUtil.getZSetRank(weekRankKey, 0, 6);

        //获取的6条数据放进集合
        List<Map<String,Object>> hotPosts = new ArrayList<>();

        for (ZSetOperations.TypedTuple typedTuple : typedTuples) {

            Map<String, Object> map = new HashMap<>();

            Object value = typedTuple.getValue(); // post的id（某个博客id）
            String postKey = "rank:post:" + value;

            map.put("id", value);
            map.put("title", redisUtil.hget(postKey, "post:title"));
            map.put("commentCount", typedTuple.getScore());
            hotPosts.add(map);
        }
        handler.put(RESULTS, hotPosts).render();

    }
}
