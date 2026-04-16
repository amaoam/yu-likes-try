package com.zmt.yulikestry.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmt.yulikestry.constant.UserConstant;
import com.zmt.yulikestry.mapper.UserMapper;
import com.zmt.yulikestry.model.entity.User;
import com.zmt.yulikestry.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
* @author ZMT
* @description 针对表【user】的数据库操作Service实现
* @createDate 2026-04-16 16:59:15
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Override
    public User getLoginUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute(UserConstant.LOGIN_USER);
    }


}




