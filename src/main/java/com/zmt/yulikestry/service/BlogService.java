package com.zmt.yulikestry.service;

import com.zmt.yulikestry.model.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zmt.yulikestry.model.vo.BlogVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author ZMT
* @description 针对表【blog】的数据库操作Service
* @createDate 2026-04-16 16:59:15
*/
public interface BlogService extends IService<Blog> {

    BlogVO getBlogVOById(long blogId, HttpServletRequest request);

    List<BlogVO> getBlogVOList(List<Blog> blogList, HttpServletRequest request);


}
