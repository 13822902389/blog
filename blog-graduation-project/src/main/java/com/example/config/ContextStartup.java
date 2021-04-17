package com.example.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.entity.Category;
import com.example.service.CategoryService;
import com.example.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.List;

@Component
public class ContextStartup implements ApplicationRunner, ServletContextAware {

    @Autowired
    CategoryService categoryService;

    ServletContext servletContext;

    @Autowired
    PostService postService;

    //项目启动时就展示分类的信息
    @Override
    public void run(ApplicationArguments args) throws Exception {
        //通过数据库的status来控制是否展示
        List<Category> categories = categoryService.list(new QueryWrapper<Category>()
                .eq("status", 0)
        );
        //servletContext.setAttribute(“xxx”, “XXX”)，在ServletContext中保存了一个域属性，域属性名称为xxx，域属性的值为XXX。
        // 请注意，如果多次调用该方法，并且使用相同的name，那么会覆盖上一次的值，这一特性与Map相同
        servletContext.setAttribute("categorys", categories);

        //项目启动就把缓存存进Redis
        postService.initWeekRank();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
