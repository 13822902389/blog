package com.example.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.common.lang.Result;
import com.example.entity.Post;
import com.example.entity.User;
import com.example.entity.UserMessage;
import com.example.shiro.AccountProfile;
import com.example.util.UploadUtil;
import com.example.vo.UserMessageVo;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户的基本信息
 */
@Controller
public class UserController extends BaseController {

    @Autowired
    UploadUtil uploadUtil;

    /**
     * 用户主页
     * @return
     */
    @GetMapping("/user/home")
    public String home() {

        User user = userService.getById(getProfileId());

        //获取当前用户发布过的所有文章
        List<Post> posts = postService.list(new QueryWrapper<Post>()
                .eq("user_id", getProfileId())
                // 30天内
                //.gt("created", DateUtil.offsetDay(new Date(), -30))
                .orderByDesc("created")
        );
        req.setAttribute("user", user);
        req.setAttribute("posts", posts);
        return "/user/home";
    }

    /**
     * 用户设置页
     * @return
     */
    @GetMapping("/user/set")
    public String set() {
        User user = userService.getById(getProfileId());
        req.setAttribute("user", user);

        return "/user/set";
    }

    /**
     * 用户设置
     * @param user
     * @return
     */
    @ResponseBody
    @PostMapping("/user/set")
    public Result doSet(User user) {

        if(StrUtil.isNotBlank(user.getAvatar())) {

            User temp = userService.getById(getProfileId());
            temp.setAvatar(user.getAvatar());
            userService.updateById(temp);

            AccountProfile profile = getProfile();
            profile.setAvatar(user.getAvatar());

            SecurityUtils.getSubject().getSession().setAttribute("profile", profile);

            return Result.success().action("/user/set#avatar");
        }

        if(StrUtil.isBlank(user.getUsername())) {
            return Result.fail("昵称不能为空");
        }
        int count = userService.count(new QueryWrapper<User>()
                .eq("username", getProfile().getUsername())
                //不等于
                .ne("id", getProfileId()));
        if(count > 0) {
            return Result.fail("改昵称已被占用");
        }

        //更新数据库用户的信息
        User temp = userService.getById(getProfileId());
        temp.setUsername(user.getUsername());
        temp.setGender(user.getGender());
        temp.setSign(user.getSign());
        userService.updateById(temp);

        //更新shiro的缓存
        AccountProfile profile = getProfile();
        profile.setUsername(temp.getUsername());
        profile.setSign(temp.getSign());
        SecurityUtils.getSubject().getSession().setAttribute("profile", profile);

        return Result.success().action("/user/set#info");
    }

    /**
     * 修改用户头像
     * @param file
     * @return
     * @throws IOException
     */
    @ResponseBody
    @PostMapping("/user/upload")
    public Result uploadAvatar(@RequestParam(value = "file") MultipartFile file) throws IOException {
        return uploadUtil.upload(UploadUtil.type_avatar, file);
    }

    /**
     * 用户更改密码
     * @param nowpass
     * @param pass
     * @param repass
     * @return
     */
    @ResponseBody
    @PostMapping("/user/repass")
    public Result repass(String nowpass, String pass, String repass) {
        if(!pass.equals(repass)) {
            return Result.fail("两次密码不相同");
        }

        User user = userService.getById(getProfileId());

        String nowPassMd5 = SecureUtil.md5(nowpass);
        if(!nowPassMd5.equals(user.getPassword())) {
            return Result.fail("密码不正确");
        }

        user.setPassword(SecureUtil.md5(pass));
        userService.updateById(user);

        return Result.success().action("/user/set#pass");

    }

    /**
     * 用户中心
     * @return
     */
    @GetMapping("/user/index")
    public String index() {
        return "/user/index";
    }

    /**
     * 用户发布的文章
     * @return
     */
    @ResponseBody
    @GetMapping("/user/public")
    public Result userP() {
        //分页根据文章创建时间排序
        IPage page = postService.page(getPage(), new QueryWrapper<Post>()
                .eq("user_id", getProfileId())
                .orderByDesc("created"));
        return Result.success(page);
    }

    /**
     * 用户收藏的文章
     * @return
     */
    @ResponseBody
    @GetMapping("/user/collection")
    public Result collection() {
        IPage page = postService.page(getPage(), new QueryWrapper<Post>()
                //post表关联user_collection表
                .inSql("id", "SELECT post_id FROM m_user_collection where user_id = " + getProfileId())
        );
        return Result.success(page);
    }

    /**
     * 我的信息
     * @return
     */
    @GetMapping("/user/mess")
    public String mess() {
        IPage<UserMessageVo> page = messageService.paging(getPage(), new QueryWrapper<UserMessage>()
                .eq("to_user_id", getProfileId())
                .orderByDesc("created")
        );

        // 把消息改成已读状态
        List<Long> ids = new ArrayList<>();
        for(UserMessageVo messageVo : page.getRecords()) {
            //未读消息
            if(messageVo.getStatus() == 0) {
                ids.add(messageVo.getId());
            }
        }
        // 批量修改成已读
        messageService.updateToReaded(ids);

        req.setAttribute("pageData", page);
        return "/user/mess";
    }

    /**
     * 删除我的信息
     * @param id
     * @param all
     * @return
     */
    @ResponseBody
    @PostMapping("/message/remove/")
    public Result msgRemove(Long id,
                            @RequestParam(defaultValue = "false") Boolean all) {

        boolean remove = messageService.remove(new QueryWrapper<UserMessage>()
                .eq("to_user_id", getProfileId())
                .eq(!all, "id", id));

        return remove ? Result.success(null) : Result.fail("你当前没有消息可删除哦！");
    }

    /**
     * 我的未读消息
     * @return
     */
    @ResponseBody
    @RequestMapping("/message/nums/")
    public Map msgNums() {

        int count = messageService.count(new QueryWrapper<UserMessage>()
                .eq("to_user_id", getProfileId())
                .eq("status", "0")
        );
        return MapUtil.builder("status", 0)
                .put("count", count).build();
    }

}
