package com.example.controller;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.common.lang.Result;
import com.example.config.RabbitConfig;
import com.example.entity.*;
import com.example.search.mq.PostMqIndexMessage;
import com.example.util.ValidationUtil;
import com.example.vo.CommentVo;
import com.example.vo.PostVo;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class PostController extends BaseController{

    /**
     * 博客分类页
     * @param id
     * @return
     */
    //    \\d表示接收的参数为固定的数字类型
    @GetMapping("/category/{id:\\d*}")
    public String category(@PathVariable(name = "id") Long id) {

        int pn = ServletRequestUtils.getIntParameter(req, "pn", 1);

        req.setAttribute("currentCategoryId", id);
        req.setAttribute("pn", pn);
        return "post/category";
    }

    /**
     * 博客详情页
     * @param id
     * @return
     */
    @GetMapping("/post/{id:\\d*}")
    public String detail(@PathVariable(name = "id") Long id) {

        //把某个博客详情信息查出
        PostVo vo = postService.selectOnePost(new QueryWrapper<Post>().eq("p.id", id));
        Assert.notNull(vo, "文章已被删除");

        //从Redis中查询阅读量
        postService.putViewCount(vo);

        // 1分页，2文章id，3用户id，排序
        IPage<CommentVo> results = commentService.paing(getPage(), vo.getId(), null, "created");

        req.setAttribute("currentCategoryId", vo.getCategoryId());
        req.setAttribute("post", vo);
        req.setAttribute("pageData", results);

        return "post/detail";
    }

    /**
     * 判断用户是否收藏了文章
     * @param pid
     * @return
     */
    @ResponseBody
    @PostMapping("/collection/find/")
    public Result collectionFind(Long pid) {
        int count = collectionService.count(new QueryWrapper<UserCollection>()
                //当前用户id
                .eq("user_id", getProfileId())
                //文章id
                .eq("post_id", pid)
        );
        return Result.success(MapUtil.of("collection", count > 0 ));
    }

    /**
     * 收藏文章
     * @param pid
     * @return
     */
    @ResponseBody
    @PostMapping("/collection/add/")
    public Result collectionAdd(Long pid) {
        //获得当前文章
        Post post = postService.getById(pid);

        //如果文章为空，则返或一条提示信息
        Assert.isTrue(post != null, "该文章已被删除！");
        int count = collectionService.count(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid)
        );
        if(count > 0) {
            return Result.fail("你已经收藏");
        }

        UserCollection collection = new UserCollection();
        collection.setUserId(getProfileId());
        collection.setPostId(pid);
        collection.setCreated(new Date());
        collection.setModified(new Date());

        collection.setPostUserId(post.getUserId());

        collectionService.save(collection);
        return Result.success();
    }

    /**
     * 取消收藏文章
     * @param pid
     * @return
     */
    @ResponseBody
    @PostMapping("/collection/remove/")
    public Result collectionRemove(Long pid) {
        Post post = postService.getById(pid);
        Assert.isTrue(post != null, "该帖子已被删除！");

        collectionService.remove(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid));
        return Result.success();
    }

    /**
     * 添加或者编辑文章页面
     * @return
     */
    @GetMapping("/post/edit")
    public String edit(){
        //HttpServletRequest的getParameter（）方法内的参数就是jsp中所定义的name的值，
        // 比如常常在login.jsp中，<input type="text" name="username"> <input type="password" name="password">
        // 索引号引起来的"username"和password"就是getParameter（）的参数， getParameter（"username"）

        String id = req.getParameter("id"); //根据id判断是否编辑或者添加（有id是编辑，没有是添加）
        if(!StringUtils.isEmpty(id)) {
            Post post = postService.getById(id);
            Assert.isTrue(post != null, "该帖子已被删除");
            Assert.isTrue(post.getUserId().longValue() == getProfileId().longValue(), "您没权限操作此文章哦");
            req.setAttribute("post", post);
        }
        //新增、编辑页面所在专栏信息 传过去给页面
        req.setAttribute("categories", categoryService.list());
        return "/post/edit";
    }

    /**
     * 提交文章
     * @param post
     * @return
     */
    @ResponseBody
    @PostMapping("/post/submit")
    public Result submit(Post post) {
        //校验提交时，post对象某些字段不能为空
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(post);
        if(validResult.hasErrors()) {
            return Result.fail(validResult.getErrors());
        }

        if(post.getId() == null) {
            post.setUserId(getProfileId());
            post.setModified(new Date());
            post.setCreated(new Date());
            post.setCommentCount(0);
            post.setEditMode(null);
            post.setLevel(0);
            post.setRecommend(false);
            post.setViewCount(0);
            post.setVoteDown(0);
            post.setVoteUp(0);
            postService.save(post);
        } else {
            Post tempPost = postService.getById(post.getId());
            Assert.isTrue(tempPost.getUserId().longValue() == getProfileId().longValue(), "您无权限编辑此文章！");
            tempPost.setTitle(post.getTitle());
            tempPost.setContent(post.getContent());
            tempPost.setCategoryId(post.getCategoryId());
            postService.updateById(tempPost);
        }

        // 通知消息给mq，告知ES更新或添加
        amqpTemplate.convertAndSend(RabbitConfig.es_exchage, RabbitConfig.es_bind_key,
                new PostMqIndexMessage(post.getId(), PostMqIndexMessage.CREATE_OR_UPDATE));
        return Result.success().action("/post/" + post.getId());
    }

    /**
     * 删除文章
     * @param id
     * @return
     */
    @ResponseBody
    @Transactional
    @PostMapping("/post/delete")
    public Result delete(Long id) {
        Post post = postService.getById(id);

        Assert.notNull(post, "该帖子已被删除");
        Assert.isTrue(post.getUserId().longValue() == getProfileId().longValue(), "您无权限删除此文章！");
        postService.removeById(id);

        // 删除相关我的消息、收藏等
        messageService.removeByMap(MapUtil.of("post_id", id));
        collectionService.removeByMap(MapUtil.of("post_id", id));

        // 通知消息给mq，告知ES删除
        amqpTemplate.convertAndSend(RabbitConfig.es_exchage, RabbitConfig.es_bind_key,
                new PostMqIndexMessage(post.getId(), PostMqIndexMessage.REMOVE));

        return Result.success().action("/user/index");
    }

    /**
     * 评论文章
     * @param jid 文章id
     * @param content 评论内容
     * @return
     */
    @ResponseBody
    @Transactional
    @PostMapping("/post/reply/")
    public Result reply(Long jid, String content) {
        Assert.notNull(jid, "找不到对应的文章");
        Assert.hasLength(content, "评论内容不能为空");

        Post post = postService.getById(jid);
        Assert.isTrue(post != null, "该文章已被删除");

        Comment comment = new Comment();
        comment.setPostId(jid);
        comment.setContent(content);
        comment.setUserId(getProfileId());
        comment.setCreated(new Date());
        comment.setModified(new Date());
        comment.setLevel(0);
        comment.setVoteDown(0);
        comment.setVoteUp(0);
        commentService.save(comment);

        // 评论数量加一
        post.setCommentCount(post.getCommentCount() + 1);
        postService.updateById(post);

        // 本周热议评论数量加一
        postService.incrCommentCountAndUnionForWeekRank(post.getId(), true);

        // 通知作者，有人评论了你的文章
        // 作者自己评论自己文章，不需要通知
        if(comment.getUserId() != post.getUserId()) {
            UserMessage message = new UserMessage();
            message.setPostId(jid);
            message.setCommentId(comment.getId());
            message.setFromUserId(getProfileId());
            message.setToUserId(post.getUserId());
            //0系统消息 1评论文章 2评论了谁的评论
            message.setType(1);
            message.setContent(content);
            message.setCreated(new Date());
            //消息状态，1:已读/ 0:未读
            message.setStatus(0);
            messageService.save(message);

            // 即时通知作者（websocket）
            wsService.sendMessCountToUser(message.getToUserId());
        }

        // 通知被@的人，有人回复了你的文章
        if(content.startsWith("@")) {
            String username = content.substring(1, content.indexOf(" "));
            System.out.println(username);

            User user = userService.getOne(new QueryWrapper<User>().eq("username", username));
            if(user != null) {
                UserMessage message = new UserMessage();
                message.setPostId(jid);
                message.setCommentId(comment.getId());
                message.setFromUserId(getProfileId());
                message.setToUserId(user.getId());
                message.setType(2);
                message.setContent(content);
                message.setCreated(new Date());
                message.setStatus(0);
                messageService.save(message);

                // 即时通知被@的用户
                wsService.sendMessCountToUser(message.getToUserId());
            }
        }
        return Result.success().action("/post/" + post.getId());
    }

    /**
     * 删除评论
     * @param id
     * @return
     */
    @ResponseBody
    @Transactional
    @PostMapping("/post/jieda-delete/")
    public Result reply(Long id) {

        Assert.notNull(id, "评论id不能为空！");

        Comment comment = commentService.getById(id);

        Assert.notNull(comment, "找不到对应评论！");

        if(comment.getUserId().longValue() != getProfileId().longValue()) {
            return Result.fail("不是你发表的评论！");
        }
        commentService.removeById(id);

        // 评论数量减一
        Post post = postService.getById(comment.getPostId());
        post.setCommentCount(post.getCommentCount() - 1);
        postService.saveOrUpdate(post);

        //评论数量减一
        postService.incrCommentCountAndUnionForWeekRank(comment.getPostId(), false);

        return Result.success(null);
    }

}
