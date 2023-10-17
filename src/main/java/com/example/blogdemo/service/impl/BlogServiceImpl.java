package com.example.blogdemo.service.impl;

import com.example.blogdemo.entity.Blog;
import com.example.blogdemo.mapper.BlogMapper;
import com.example.blogdemo.service.BlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zlx
 * @since 2023-09-07
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

}
