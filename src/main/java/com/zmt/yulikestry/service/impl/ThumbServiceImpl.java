package com.zmt.yulikestry.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmt.yulikestry.mapper.ThumbMapper;
import com.zmt.yulikestry.model.dto.Thumb.DoThumbRequest;
import com.zmt.yulikestry.model.entity.Blog;
import com.zmt.yulikestry.model.entity.Thumb;
import com.zmt.yulikestry.model.entity.User;
import com.zmt.yulikestry.service.BlogService;
import com.zmt.yulikestry.service.ThumbService;
import com.zmt.yulikestry.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
* @author ZMT
* @description 针对表【thumb】的数据库操作Service实现
* @createDate 2026-04-16 16:59:15
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class ThumbServiceImpl extends ServiceImpl<ThumbMapper, Thumb> implements ThumbService {

    private final UserService userService;
    
    private final TransactionTemplate transactionTemplate;
    
    private final BlogService blogService;

    @Override
    public Boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if(request==null||doThumbRequest==null) throw new RuntimeException("参数错误");
        User loginUser = userService.getLoginUser(request);
        synchronized (loginUser.getId().toString().intern()){
            return transactionTemplate.execute(status->{
                Long blodId = doThumbRequest.getBlogId();
                boolean exists = this.lambdaQuery()
                        .eq(Thumb::getUserId,loginUser.getId())
                        .eq(Thumb::getBlogId,blodId)
                        .exists();
                if(exists){
                    throw new RuntimeException("用户已点赞");
                }
                boolean update = blogService.lambdaUpdate()
                        .eq(Blog::getId,blodId)
                        .setSql("thumbCount= thumbCount+1")
                        .update();
                Thumb thumb = new Thumb();
                thumb.setUserId(loginUser.getId());
                thumb.setBlogId(blodId);
                return update&&this.save(thumb);
            });
        }
    }

    @Override
    public Boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if(request==null||doThumbRequest==null) throw new RuntimeException("参数错误");
        User loginUser = userService.getLoginUser(request);
        synchronized (loginUser.getId().toString().intern()){
            return transactionTemplate.execute(status->{
                Long blogId = doThumbRequest.getBlogId();
                Thumb thumb = this.lambdaQuery()
                        .eq(Thumb::getBlogId,blogId)
                        .eq(Thumb::getId,loginUser.getId())
                        .one();
                if(thumb==null) throw new RuntimeException("用户未点赞");
                
                boolean update = blogService.lambdaUpdate()
                        .eq(Blog::getId,blogId)
                        .setSql("thumbCount = thumbCount-1")
                        .update();
                return update&&this.removeById(thumb.getId());
            });
        }
    }
}




