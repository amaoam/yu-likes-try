package com.zmt.yulikestry.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmt.yulikestry.constant.RedisLuaScriptConstant;
import com.zmt.yulikestry.mapper.ThumbMapper;
import com.zmt.yulikestry.model.dto.Thumb.DoThumbRequest;
import com.zmt.yulikestry.model.entity.Blog;
import com.zmt.yulikestry.model.entity.Thumb;
import com.zmt.yulikestry.model.entity.User;
import com.zmt.yulikestry.model.enums.LuaStatusEnum;
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

import java.util.Arrays;

/**
* @author ZMT
* @description 针对表【thumb】的数据库操作Service实现
* @createDate 2026-04-16 16:59:15
*/
@Service("thumbServiceRedis")
@Slf4j
@RequiredArgsConstructor
public class ThumbServiceRedisImpl extends ServiceImpl<ThumbMapper, Thumb> implements ThumbService {

    private final UserService userService;
    
    private final RedisTemplate<String,Object> redisTemplate;
    

    @Override
    public Boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if(doThumbRequest == null||doThumbRequest.getBlogId()==null) throw new RuntimeException("参数错误");
        User loginUser = userService.getLoginUser(request);
        Long blogId = doThumbRequest.getBlogId();
        String timeSlice = getTimeSlice();
        String tempThumbKey = RedisKeyUtil.getTempThumbKey(timeSlice);
        String userThumbKey = RedisKeyUtil.getUserThumbKey(loginUser.getId());
        long result = redisTemplate.execute(
                RedisLuaScriptConstant.THUMB_SCRIPT,
                Arrays.asList(tempThumbKey,userThumbKey),
                loginUser.getId(),
                blogId
        );
        if(LuaStatusEnum.FAIL.getValue()==result) throw new RuntimeException("用户已点赞");
        return LuaStatusEnum.SUCCESS.getValue()==result;
    }

    @Override
    public Boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if(doThumbRequest == null||doThumbRequest.getBlogId()==null) throw new RuntimeException("参数错误");
        User loginUser = userService.getLoginUser(request);
        Long blogId = doThumbRequest.getBlogId();
        String timeSlice = getTimeSlice();
        String tempThumbKey = RedisKeyUtil.getTempThumbKey(timeSlice);
        String userThumbKey = RedisKeyUtil.getUserThumbKey(loginUser.getId());
        long result = redisTemplate.execute(
                RedisLuaScriptConstant.UNTHUMB_SCRIPT,
                Arrays.asList(tempThumbKey,userThumbKey),
                loginUser.getId(),
                blogId
        );
        if(LuaStatusEnum.FAIL.getValue()==result) throw new RuntimeException("用户未点赞");
        return LuaStatusEnum.SUCCESS.getValue()==result;
    }

    @Override
    public Boolean hasThumb(Long blogId, Long userId) {
        return redisTemplate.opsForHash().hasKey(RedisKeyUtil.getUserThumbKey(userId),blogId.toString());
    }
    
    private String getTimeSlice(){
        DateTime nowDate = DateUtil.date();
        return DateUtil.format(nowDate,"HH:mm:")+(DateUtil.second(nowDate)/10)*10;
    }
    


}




