package com.zmt.yulikestry.mapper;

import com.zmt.yulikestry.model.entity.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
* @author ZMT
* @description 针对表【blog】的数据库操作Mapper
* @createDate 2026-04-16 16:59:15
* @Entity com.zmt.yulikestry.model.entity.Blog
*/
public interface BlogMapper extends BaseMapper<Blog> {
    
    void batchUpdateThumbCount(@Param("countMap") Map<Long, Long> countMap);
}




