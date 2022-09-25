package com.lin.srb.feign.client.fallback;

import com.lin.common.exception.BusinessException;
import com.lin.srb.feign.client.CoreUserInfoClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CoreUserInfoClientFallback implements CoreUserInfoClient {
    @Override
    public boolean checkMobile(String mobile,Integer userType) {
        log.error("短信服务远程调用失败，服务熔断");
        throw new BusinessException("远程调用失败，服务熔断",-503);
        //return false;
    }
}