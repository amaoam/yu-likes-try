package com.zmt.yulikestry.service;

import com.zmt.yulikestry.model.dto.Thumb.DoThumbRequest;
import com.zmt.yulikestry.model.entity.Thumb;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author ZMT
* @description 针对表【thumb】的数据库操作Service
* @createDate 2026-04-16 16:59:15
*/
public interface ThumbService extends IService<Thumb> {

    /**
     * 点赞  
     * @param doThumbRequest
     * @param request
     * @return {@link Boolean }
     */
    Boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request);

    /**
     * 取消点赞  
     * @param doThumbRequest
     * @param request
     * @return {@link Boolean }
     */
    Boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request);


}
