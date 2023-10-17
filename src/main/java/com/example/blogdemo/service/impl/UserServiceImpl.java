package com.example.blogdemo.service.impl;

import com.example.blogdemo.entity.User;
import com.example.blogdemo.mapper.UserMapper;
import com.example.blogdemo.service.UserService;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
