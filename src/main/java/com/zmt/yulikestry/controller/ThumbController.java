package com.zmt.yulikestry.controller;

import com.zmt.yulikestry.common.BaseResponse;
import com.zmt.yulikestry.common.ResultUtils;
import com.zmt.yulikestry.model.dto.Thumb.DoThumbRequest;
import com.zmt.yulikestry.service.ThumbService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: ZMT
 * @date: 2026/4/16
 * @Description:
 */
@RestController
@RequestMapping("thumb")
@RequiredArgsConstructor
public class ThumbController {
    
    private final ThumbService thumbService;
    
    @PostMapping("/do")
    public BaseResponse<Boolean> doThumb(@RequestBody DoThumbRequest doThumbRequest, HttpServletRequest request){
        Boolean success = thumbService.doThumb(doThumbRequest,request);
        return ResultUtils.success(success);
    }
    
    @PostMapping("/undo")
    public BaseResponse<Boolean> undoThumb(@RequestBody DoThumbRequest doThumbRequest,HttpServletRequest request){
        Boolean success = thumbService.undoThumb(doThumbRequest,request);
        return ResultUtils.success(success);
    }
    
}
