package com.lin.srb.jobs.clean;

import com.lin.common.result.R;
import com.lin.srb.base.constant.RedisConstant;
import com.lin.srb.feign.client.RemoveImgsClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 自定义Job，实现定时清理垃圾图片
 */
@Component
public class CleanImgJob {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RemoveImgsClient removeImgsClient;

    public void cleanImg() {
        //已上传云的附件图片
        Set<String> cloudImg = redisTemplate.opsForSet().members(RedisConstant.ATTACH_RESOURCES);
        if (cloudImg == null || cloudImg.size() == 0) {
            System.out.println("当前云上没有垃圾图片");
            return;
        }
        //存入数据库的附件图片
        Set<String> dbImg = redisTemplate.opsForSet().members(RedisConstant.ATTACH_DB_RESOURCES);
        //得到两个集合的差集，也就是云要删除的垃圾附件图片
        cloudImg.remove(dbImg);
        if (cloudImg == null || cloudImg.size() == 0) {
            System.out.println("当前云上没有垃圾图片");
            return;
        }
        R removeTrashRes = removeImgsClient.batchRemoveFiles(cloudImg);
        System.out.println(removeTrashRes.getMessage());
    }
}