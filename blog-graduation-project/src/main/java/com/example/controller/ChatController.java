package com.example.controller;

import cn.hutool.core.map.MapUtil;
import com.example.common.lang.Consts;
import com.example.common.lang.Result;
import com.example.im.vo.ImUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 群聊控制器
 */
@RestController
@RequestMapping("/chat")
public class ChatController extends BaseController {

    /**
     * 获取群聊里的个人信息、群信息
     * @return
     */
    @GetMapping("/getMineAndGroupData")
    public Result getMineAndGroupData() {

        //默认群
        Map<String, Object> group = new HashMap<>();
        group.put("name", "谈心木屋");
        group.put("type", "group");
        group.put("avatar", "http://pic1.win4000.com/wallpaper/2020-07-20/5f1566fd080fc.jpg");
        group.put("id", Consts.IM_GROUP_ID);
        group.put("members", 0);

        //个人信息
        ImUser user = chatService.getCurrentUser();
        return Result.success(MapUtil.builder()
                .put("group", group)
                .put("mine", user)
                .map());
    }

    /**
     * 获取群聊历史信息
     * @return
     */
    @GetMapping("/getGroupHistoryMsg")
    public Result getGroupHistoryMsg() {

        List<Object> messages = chatService.getGroupHistoryMsg(0);
        return Result.success(messages);
    }

}
