package com.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.lang.Result;
import com.example.entity.Post;
import com.example.vo.PostVo;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 超级管理员的一些操作
 */
@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {

    /**
     * 加精操作
     * @param id 当前文章id
     * @param rank 0表示取消，1表示操作
     * @param field 判断是加精还是置顶
     * @return
     */
    @ResponseBody
    @PostMapping("/jie-set")
    public Result jetSet(Long id, Integer rank, String field) {

        Post post = postService.getById(id);
        Assert.notNull(post, "该帖子已被删除");

        if("delete".equals(field)) {
            postService.removeById(id);
            return Result.success();

            //判断是否加精 1是 0否
        } else if("status".equals(field)) {
            post.setRecommend(rank == 1);

            //判断是否置顶
        }  else if("stick".equals(field)) {
            post.setLevel(rank);
        }
        postService.updateById(post);
        return Result.success();
    }

    /**
     * 只有管理员才能 初始化ES的所有数据（将mysql的post表的所有数据存进ES）分页同步进ES
     * @return
     */
    @ResponseBody
    @PostMapping("/initEsData")
    public Result initEsData() {

        int size = 10000;
        Page page = new Page();
        page.setSize(size);

        long total = 0;

        for (int i = 1; i < 1000; i ++) {
            page.setCurrent(i);
                                                //参数为空代表把post表中的数据全部查出来
            IPage<PostVo> paging = postService.paging(page, null, null, null, null, null);

            int num = searchService.initEsData(paging.getRecords());

            total += num;

            // 当一页查不出10000条的时候，说明是最后一页了
            if(paging.getRecords().size() < size) {
                break;
            }
        }
        return Result.success("ES索引初始化成功，共 " + total + " 条记录！", null);
    }


}
