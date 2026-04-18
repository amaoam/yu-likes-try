package com.zmt.yulikestry.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmt.yulikestry.constant.ThumbConstant;
import com.zmt.yulikestry.mapper.BlogMapper;
import com.zmt.yulikestry.model.entity.Blog;
import com.zmt.yulikestry.model.entity.Thumb;
import com.zmt.yulikestry.model.entity.User;
import com.zmt.yulikestry.model.vo.BlogVO;
import com.zmt.yulikestry.service.BlogService;
import com.zmt.yulikestry.service.ThumbService;
import com.zmt.yulikestry.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
* @author ZMT
* @description 针对表【blog】的数据库操作Service实现
* @createDate 2026-04-16 16:59:15
*/
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {
    
    @Resource
    private  UserService userService;
    
    @Resource
    @Lazy
    private ThumbService thumbService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public BlogVO getBlogVOById(long blogId, HttpServletRequest request) {
        Blog blog = this.getById(blogId);
        User loginUser = userService.getLoginUser(request);
        return this.getBlogVo(blog,loginUser);
    }
    

    private BlogVO getBlogVo(Blog blog,User loginUser){
        BlogVO blogVO = new BlogVO();
        BeanUtil.copyProperties(blog,blogVO);
        if(loginUser==null) return  blogVO;
//        Thumb thumb = thumbService.lambdaQuery()
//                .eq(Thumb::getUserId,loginUser.getId())
//                .eq(Thumb::getBlogId,blog.getId())
//                .one();
//        blogVO.setHasThumb(thumb!=null);
        Boolean exist = thumbService.hasThumb(blog.getId(),loginUser.getId());
        blogVO.setHasThumb(exist);
        return  blogVO;
    }

    @Override
    public List<BlogVO> getBlogVOList(List<Blog> blogList, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Map<Long, Boolean> blogIdHasThumbMap = new HashMap<>();
//        if (ObjUtil.isNotEmpty(loginUser)) {
//            Set<Long> blogIdSet = blogList.stream().map(Blog::getId).collect(Collectors.toSet());
//            List<Thumb> thumbList = thumbService.lambdaQuery()
//                    .eq(Thumb::getUserId, loginUser.getId())
//                    .in(Thumb::getBlogId, blogIdSet)
//                    .list();
//            thumbList.forEach(blogThumb -> blogIdHasThumbMap.put(blogThumb.getBlogId(), true));
//        }
        if(ObjUtil.isNotEmpty(loginUser)){
            List<Object> blogIdList = blogList.stream().map(blog -> blog.getId().toString()).collect(Collectors.toList());
            List<Object> thumbList = redisTemplate.opsForHash().multiGet(ThumbConstant.USER_THUMB_KEY_PREFIX+loginUser.getId(),blogIdList);
            for(int i = 0;i<thumbList.size();i++){
                if(thumbList.get(i)==null) continue;
                blogIdHasThumbMap.put(Long.valueOf(blogIdList.get(i).toString()),true);
            }
        }
        return  blogList.stream().map(
                blog -> {
                    BlogVO blogVO = BeanUtil.copyProperties(blog,BlogVO.class);
                    blogVO.setHasThumb(blogIdHasThumbMap.get(blog.getId()));
                    return blogVO;
                }
        ).toList();
    }
    
    
    
}




