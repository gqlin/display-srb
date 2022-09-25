package com.lin.srb.uaa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.lin.srb", "com.lin.common"})
public class SrbUaaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SrbUaaApplication.class, args);
    }
}
