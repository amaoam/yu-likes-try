package com.zmt.yulikestry.controller;

import com.zmt.yulikestry.common.BaseResponse;
import com.zmt.yulikestry.common.ResultUtils;
import com.zmt.yulikestry.model.entity.Blog;
import com.zmt.yulikestry.model.vo.BlogVO;
import com.zmt.yulikestry.service.BlogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Results;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * @author: ZMT
 * @date: 2026/4/16
 * @Description:
 */
@RestController
@RequestMapping("blog")
@RequiredArgsConstructor
public class BlogController {
    
    private final BlogService blogService;
    
    @GetMapping("/get")
    public BaseResponse<BlogVO> get(long blogId, HttpServletRequest request){
        BlogVO blogVO = blogService.getBlogVOById(blogId,request);
        return ResultUtils.success(blogVO);
    }

    @GetMapping("/list")
    public BaseResponse<List<BlogVO>> list(HttpServletRequest request) {
        List<Blog> blogList = blogService.list();
        List<BlogVO> blogVOList = blogService.getBlogVOList(blogList, request);
        return ResultUtils.success(blogVOList);
    }
    
}
