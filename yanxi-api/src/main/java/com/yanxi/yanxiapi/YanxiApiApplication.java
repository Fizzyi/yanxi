package com.yanxi.yanxiapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class YanxiApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(YanxiApiApplication.class, args);
    }

}
