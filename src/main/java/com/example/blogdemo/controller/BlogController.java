package com.example.blogdemo.controller;


import cn.hutool.core.bean.BeanUtil;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blogdemo.common.lang.Result;
import com.example.blogdemo.entity.Blog;
import com.example.blogdemo.service.BlogService;
import com.example.blogdemo.util.ShiroUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zlx
 * @since 2023-09-07
 */
@Slf4j  // Slf4j表示打印日志
@RestController
public class BlogController {
    @Autowired
    BlogService blogService;


    /**
     * 分页方法, 对博客进行分页
     */
    @GetMapping("/blogs")
    public Result blogs(Integer currentPage){
        if (currentPage == null || currentPage<1){
            currentPage = 1;
        }
        /*
        page实体, 表示页面
        currentPage表示起始页面id号
        后面的5表示一个页面5条显示数据
         */

        Page page = new Page(currentPage,5); //自带的分页实体, 注意导入的是Mybatisplus里面的类
        // 通过Ipage实体类, 让显示的博客按照日期进行排序
        // 最后返回对象数组
        /* ?????? */
        IPage pagedata = blogService.page(page,new QueryWrapper<Blog>().orderByDesc("created"));
        return Result.succ(pagedata);
    }

    /**
     * 获取博客详情
     */
    @GetMapping("/blog/{id}")
    public Result detail(@PathVariable(name = "id") long id ){
        Blog blog = blogService.getById(id); // 获取到对应id实体
        Assert.notNull(blog,"该博客已删除"); //不存在判断断言
        log.info("获取到博客实体"+blog);
        return Result.succ(blog); //向前端传递博客实体
    }

    /**
     * 博客编写或者添加
     * 带@Validated @RequestBody  这两个注解就知道是传回数据
     * @return
     */
    @RequiresAuthentication // 需要登录后才能进行编辑
    @PostMapping("/blog/edit")
    public Result edit(@Validated @RequestBody Blog blog){
        System.out.println("当前博客:"+blog.toString());
        Blog blogTemp = null; //新的博客(写的或者要更新的)

        // 进行判断, 看博客是添加还是更新
        if (blog.getId() != null){
            // 更新
            blogTemp = blogService.getById(blog.getId());

            // 用断言确保只能修改自己的文章, 凭借用户登录的token进行判断
            // 该代码的含义是当前blog的用户id和之前登录的用户id是否一致, 不一致抛出异常输出""
            Assert.isTrue(blogTemp.getUserId() == ShiroUtil.getRec().getId(), "不能修改他人数据");

        }else{
            // 添加
            blogTemp = new Blog();
            blogTemp.setUserId(ShiroUtil.getRec().getId());
            blogTemp.setCreated(LocalDateTime.now()); // LocalDateTime.now() 直接获取当前时间
            blogTemp.setStatus(0); //是设置的状态???
        }

        // 执行覆盖/添加操作
        // 使用beanUtil类进行覆盖操作, 把blogTemp赋值给blog, 只覆盖部分参数 , 引用的不覆盖
        BeanUtil.copyProperties(blog,blogTemp,"id","userId","created","status");
        /*
        这里的忽略值不能写错或者少写
        如果出现有字段没有对应的情况 , 会直接报错
        Field ‘字段名‘ doesn‘t have a default value
         */
        blogService.saveOrUpdate(blogTemp); // 直接调用自带方法更新或者升级

        return Result.succ("操作成功",null);
    }
/*
基本完成, 只是不知道为啥数据库添加数据id是从10开始而不是1开始
 */
}
