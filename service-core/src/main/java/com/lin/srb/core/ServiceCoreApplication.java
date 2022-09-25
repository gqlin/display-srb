package com.lin.srb.core;

import com.lin.srb.feign.client.TokenArrangeClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@ComponentScan({"com.lin.srb", "com.lin.common"})
@EnableFeignClients(clients = {TokenArrangeClient.class})
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ServiceCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCoreApplication.class, args);
    }
}
