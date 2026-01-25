package com.election.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.election.system.mapper")
@EnableScheduling
public class ElectionSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElectionSystemApplication.class, args);
        System.out.println("班级干部评选系统启动成功！");
    }
}
