package com.zmt.yulikestry.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmt.yulikestry.constant.ThumbConstant;
import com.zmt.yulikestry.manager.cache.CacheManager;
import com.zmt.yulikestry.mapper.ThumbMapper;
import com.zmt.yulikestry.model.dto.Thumb.DoThumbRequest;
import com.zmt.yulikestry.model.entity.Blog;
import com.zmt.yulikestry.model.entity.Thumb;
import com.zmt.yulikestry.model.entity.User;
import com.zmt.yulikestry.service.BlogService;
import com.zmt.yulikestry.service.ThumbService;
import com.zmt.yulikestry.service.UserService;
import com.zmt.yulikestry.util.RedisKeyUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
* @author ZMT
* @description 针对表【thumb】的数据库操作Service实现
* @createDate 2026-04-16 16:59:15
*/
@Service("thumbServiceLocalCache")
@Slf4j
@RequiredArgsConstructor
public class ThumbServiceImpl extends ServiceImpl<ThumbMapper, Thumb> implements ThumbService {

    private final UserService userService;
    
    private final TransactionTemplate transactionTemplate;
    
    private final BlogService blogService;
    
    private final RedisTemplate<String,Object> redisTemplate;

    private final CacheManager cacheManager;

    @Override   
    public Boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if(request==null||doThumbRequest==null) throw new RuntimeException("参数错误");
        User loginUser = userService.getLoginUser(request);
        synchronized (loginUser.getId().toString().intern()){
            return transactionTemplate.execute(status->{
                Long blogId = doThumbRequest.getBlogId();
//                boolean exists = this.lambdaQuery()
//                        .eq(Thumb::getUserId,loginUser.getId())
//                        .eq(Thumb::getBlogId,blogId)
//                        .exists();
                Boolean exists = this.hasThumb(blogId, loginUser.getId());
                if(exists){
                    throw new RuntimeException("用户已点赞");
                }
                boolean update = blogService.lambdaUpdate()
                        .eq(Blog::getId,blogId)
                        .setSql("thumbCount= thumbCount+1")
                        .update();
                Thumb thumb = new Thumb();
                thumb.setUserId(loginUser.getId());
                thumb.setBlogId(blogId);
//                return update&&this.save(thumb);
                boolean success = update && this.save(thumb);
                // 点赞记录存入 Redis  
                if (success) {
//                    redisTemplate.opsForHash().put(RedisKeyUtil.getUserThumbKey(loginUser.getId()), blogId.toString(), thumb.getId());
                    String hashKey = ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId();
                    String fieldKey = blogId.toString();
                    Long realThumbId = thumb.getId();
                    redisTemplate.opsForHash().put(hashKey, fieldKey, realThumbId);
                    cacheManager.putIfPresent(hashKey, fieldKey, realThumbId);
                }
                // 更新成功才执行  
                return success;

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
//                Thumb thumb = this.lambdaQuery()
//                        .eq(Thumb::getBlogId,blogId)
//                        .eq(Thumb::getId,loginUser.getId())
//                        .one();
//                if(thumb==null) throw new RuntimeException("用户未点赞");
//                Object thumbIdObj = redisTemplate.opsForHash().get(RedisKeyUtil.getUserThumbKey(loginUser.getId()),blogId.toString());
//                if(thumbIdObj==null){
//                    throw new RuntimeException("用户未点赞");
//                }
                Object thumbIdObj = cacheManager.get(ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId(), blogId.toString());
                if (thumbIdObj == null || thumbIdObj.equals(ThumbConstant.UN_THUMB_CONSTANT)) {
                    throw new RuntimeException("用户未点赞");
                }
                Long thumbId = Long.valueOf(thumbIdObj.toString());
                boolean update = blogService.lambdaUpdate()
                        .eq(Blog::getId,blogId)
                        .setSql("thumbCount = thumbCount-1")
                        .update();
                
                boolean success =  update&&this.removeById(thumbId);
                if(success){
//                    redisTemplate.opsForHash().delete(RedisKeyUtil.getUserThumbKey(loginUser.getId()),blogId.toString());
                    String hashKey = ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId();
                    String fieldKey = blogId.toString();
                    redisTemplate.opsForHash().delete(hashKey, fieldKey);
                    cacheManager.putIfPresent(hashKey, fieldKey, ThumbConstant.UN_THUMB_CONSTANT);

                }
                return success;
            });
        }
    }

    @Override
    public Boolean hasThumb(Long blogId, Long userId) {
//        return redisTemplate.opsForHash().hasKey(RedisKeyUtil.getUserThumbKey(userId),blogId.toString());
        Object thumbIdObj = cacheManager.get(ThumbConstant.USER_THUMB_KEY_PREFIX + userId, blogId.toString());
        if (thumbIdObj == null) {
            return false;
        }
        Long thumbId = (Long) thumbIdObj;
        return !thumbId.equals(ThumbConstant.UN_THUMB_CONSTANT);
    }


}




