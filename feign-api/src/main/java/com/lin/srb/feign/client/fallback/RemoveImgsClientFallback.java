package com.lin.srb.feign.client.fallback;

import com.lin.common.exception.BusinessException;
import com.lin.common.result.R;
import com.lin.srb.feign.client.RemoveImgsClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class RemoveImgsClientFallback implements RemoveImgsClient {
    @Override
    public R batchRemoveFiles(Set<String> urlSet) {
        log.error("文件服务远程调用失败，服务熔断");
        throw new BusinessException("远程调用失败，服务熔断",-503);
        //return false;
    }
}