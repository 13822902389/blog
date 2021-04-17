package com.example.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.example.common.lang.Consts;
import com.example.im.vo.ImMess;
import com.example.im.vo.ImUser;
import com.example.service.ChatService;
import com.example.shiro.AccountProfile;
import com.example.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("chatService")
public class ChatServiceImpl implements ChatService {

    @Autowired
    RedisUtil redisUtil;

    @Override
    public ImUser getCurrentUser() {
        //获取当前登录用户
        AccountProfile profile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        ImUser user = new ImUser();

        if(profile != null) {
            user.setId(profile.getId());
            user.setAvatar(profile.getAvatar());
            user.setUsername(profile.getUsername());
            user.setStatus(ImUser.ONLINE_STATUS);

        } else {
            // 匿名用户处理
            user.setAvatar("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=692986003,220953439&fm=26&gp=0.jpg");
            //从session中获取用户id，不为空就直接set，否则给个随机id
            Long imUserId = (Long) SecurityUtils.getSubject().getSession().getAttribute("imUserId");
            user.setId(imUserId != null ? imUserId : RandomUtil.randomLong());
            //id为空时，就把随机的用户id 存进session中（匿名用户id）
            SecurityUtils.getSubject().getSession().setAttribute("imUserId", user.getId());

            user.setSign("我只是一个游客!");
            user.setUsername("游客");
            user.setStatus(ImUser.ONLINE_STATUS);
        }

        return user;
    }

    @Override
    public void setGroupHistoryMsg(ImMess imMess) {
        redisUtil.lSet(Consts.IM_GROUP_HISTROY_MSG_KEY, imMess, 24 * 60 * 60);
    }

    @Override
    public List<Object> getGroupHistoryMsg(int count) {
        //获取list缓存的长度
        long length = redisUtil.lGetListSize(Consts.IM_GROUP_HISTROY_MSG_KEY);
        return redisUtil.lGet(Consts.IM_GROUP_HISTROY_MSG_KEY, length - count < 0 ? 0 : length - count, length);
    }
}
