package com.qizegao.wxmini;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@ServletComponentScan
@SpringBootApplication
@EnableAsync
public class WxMiniApplication {

    public static void main(String[] args) {
        SpringApplication.run(WxMiniApplication.class, args);
    }

}
