package com.zmt.yulikestry.service;

import com.zmt.yulikestry.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author ZMT
* @description 针对表【user】的数据库操作Service
* @createDate 2026-04-16 16:59:15
*/
public interface UserService extends IService<User> {

    User getLoginUser(HttpServletRequest request);
}
