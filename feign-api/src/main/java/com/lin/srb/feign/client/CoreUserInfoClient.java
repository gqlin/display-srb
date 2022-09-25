package com.lin.srb.feign.client;

import com.lin.srb.feign.client.fallback.CoreUserInfoClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-core", fallback = CoreUserInfoClientFallback.class)
public interface CoreUserInfoClient {

    //远程调用com.lin.srb.core.controller.api的checkMobile
    @GetMapping("/api/core/userInfo/checkMobile/{mobile}/{userType}")
    boolean checkMobile(@PathVariable String mobile, @PathVariable Integer userType);
}