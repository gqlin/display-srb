package com.lin.srb.oss.controller.api;

import com.lin.common.exception.BusinessException;
import com.lin.common.result.R;
import com.lin.common.result.ResponseEnum;
import com.lin.srb.base.constant.RedisConstant;
import com.lin.srb.oss.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Api(tags = "阿里云文件管理")
@RestController
@RequestMapping("/api/oss/file")
@Slf4j
public class FileController {
    @Resource
    private FileService fileService;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 文件上传
     */
    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public R upload(
            @ApiParam(value = "文件", required = true)
            @RequestParam("file") MultipartFile file,
            @ApiParam(value = "模块", required = true)
            @RequestParam("module") String module) {
        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String uploadUrl = fileService.upload(inputStream, module, originalFilename);
            //已上传云的图片文件，文件名存入redis的RedisConstant.ATTACH_PIC_RESOURCES集合中
            redisTemplate.opsForSet().add(RedisConstant.ATTACH_RESOURCES, uploadUrl);
            //返回r对象
            return R.ok().message("文件上传成功").data("url", uploadUrl);
        } catch (IOException e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR, e);
        }
    }

    @ApiOperation("删除OSS文件")
    @DeleteMapping("/remove")
    public R remove(
            @ApiParam(value = "要删除的文件路径", required = true)
            @RequestParam("url") String url) {
        fileService.removeFile(url);
        redisTemplate.opsForSet().remove(RedisConstant.ATTACH_RESOURCES, url);
        return R.ok().message("删除成功");
    }

    @ApiOperation("批量删除OSS文件")
    @DeleteMapping("/batchRemoveFiles")
    public R batchRemoveFiles(
            @ApiParam(value = "要删除的文件路径集合", required = true)
            @RequestBody Set<String> urlSet) {
        fileService.batchRemoveFiles(urlSet);
        //redis中移除RedisConstant.ATTACH_PIC_RESOURCES的垃圾图片
        for (String trashUrl : urlSet) {
            redisTemplate.opsForSet().remove(RedisConstant.ATTACH_RESOURCES, trashUrl);
        }
        return R.ok().message("批量删除成功");
    }
}