package com.lin.srb.feign.client;

import com.lin.srb.feign.client.fallback.TokenArrangeClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "srb-uaa", fallback = TokenArrangeClientFallback.class)
public interface TokenArrangeClient {

    @GetMapping("/uaa/createToken/{userId}/{userName}/{userType}")
    String createToken(@PathVariable Long userId, @PathVariable String userName, @PathVariable Integer userType);
}