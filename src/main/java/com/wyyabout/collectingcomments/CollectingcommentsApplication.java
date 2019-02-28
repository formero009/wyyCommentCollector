package com.wyyabout.collectingcomments;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan({"com.wyyabout.collectingcomments.mapper"})
public class CollectingcommentsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CollectingcommentsApplication.class, args);
    }
}