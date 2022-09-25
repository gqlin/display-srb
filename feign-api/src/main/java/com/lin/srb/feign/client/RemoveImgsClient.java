package com.lin.srb.feign.client;

import com.lin.common.result.R;
import com.lin.srb.feign.client.fallback.RemoveImgsClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

@FeignClient(value = "service-oss", fallback = RemoveImgsClientFallback.class)
public interface RemoveImgsClient {

    //远程调用com.lin.srb.oss.controller.api的batchRemoveFiles
    @DeleteMapping ("/api/oss/file/batchRemoveFiles")
    R batchRemoveFiles(@RequestBody Set<String> urlSet);


}