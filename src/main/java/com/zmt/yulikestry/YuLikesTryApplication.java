package com.zmt.yulikestry;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zmt.yulikestry.mapper")
public class YuLikesTryApplication {

    public static void main(String[] args) {
        SpringApplication.run(YuLikesTryApplication.class, args);
    }

}
