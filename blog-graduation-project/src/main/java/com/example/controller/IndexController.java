package com.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController extends BaseController{

    /**
     * 博客首页
     * @return
     */
    @RequestMapping({"", "/", "index"})
    public String index() {

        // 1分页信息 2分类 3用户 4置顶  5精选 6排序
        IPage results = postService.paging(getPage(), null, null, null, null, "created");

        req.setAttribute("pageData", results);
        req.setAttribute("currentCategoryId", 0);
        return "index";
    }

    /**
     * 首页博客搜索
     * @param q
     * @return
     */
    @RequestMapping("/search")
    public String search(String q) {
        //关键字搜索并分页
        IPage pageData = searchService.search(getPage(), q);

        req.setAttribute("q", q);
        req.setAttribute("pageData", pageData);
        return "search";
    }

}
