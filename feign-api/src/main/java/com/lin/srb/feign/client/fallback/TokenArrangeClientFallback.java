package com.lin.srb.feign.client.fallback;

import com.lin.common.exception.BusinessException;
import com.lin.srb.feign.client.TokenArrangeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @description：
 * @date： 2022/9/14 19:46
 * @author：gqlin
 */
@Service
@Slf4j
public class TokenArrangeClientFallback implements TokenArrangeClient {
    @Override
    public String createToken(Long userId, String userName,Integer userType) {
        log.error("UAA颁布token的服务远程调用失败，服务熔断");
        throw new BusinessException("远程创建token调用失败，服务熔断",-503);
        //return false;
    }
}
