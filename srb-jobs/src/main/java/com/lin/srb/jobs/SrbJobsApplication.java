package com.lin.srb.jobs;

import com.lin.srb.feign.client.RemoveImgsClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.lin.srb", "com.lin.common"})
@EnableFeignClients(clients = {RemoveImgsClient.class})
public class SrbJobsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SrbJobsApplication.class, args);
    }
}
