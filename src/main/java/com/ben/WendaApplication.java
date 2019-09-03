package com.ben;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @ClassName: WendaApplication
 * @author: benjamin
 * @version: 1.0
 * @description: SpringBoot的启动类
 * @createTime: 2019/08/17/09:13
 */

@SpringBootApplication
public class WendaApplication {
    public static void main(String[] args) {

        System.out.println(SpringApplication.run(WendaApplication.class,args));

    }

}
