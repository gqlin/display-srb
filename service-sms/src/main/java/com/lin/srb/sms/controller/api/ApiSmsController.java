package com.lin.srb.sms.controller.api;

import com.lin.common.exception.Assert;
import com.lin.common.result.R;
import com.lin.common.result.ResponseEnum;
import com.lin.common.util.RandomUtils;
import com.lin.common.util.RegexValidateUtils;
import com.lin.srb.feign.client.CoreUserInfoClient;
import com.lin.srb.sms.service.SmsService;
import com.lin.srb.sms.util.SmsProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/sms")
@Api(tags = "短信管理")
@Slf4j
public class ApiSmsController {
    @Resource
    private SmsService smsService;
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CoreUserInfoClient coreUserInfoClient;

    @ApiOperation("获取验证码")
    @GetMapping("/send/{mobile}/{userType}")
    public R send(
            @ApiParam(value = "手机号", required = true)
            @PathVariable String mobile,
            @PathVariable Integer userType) {

        //MOBILE_NULL_ERROR(-202, "手机号不能为空"),
        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        //MOBILE_ERROR(-203, "手机号不正确"),
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResponseEnum.MOBILE_ERROR);

        //手机号是否注册
        boolean result = coreUserInfoClient.checkMobile(mobile,userType);
        System.out.println("result = " + result);
        Assert.isTrue(result == false, ResponseEnum.MOBILE_EXIST_ERROR);

        // 如果短信还在有效期内（2）分钟，不再发送，提醒用户短信已发出
        String ifExist = (String) redisTemplate.opsForValue().get("srb:sms:code:" + mobile);
        if (ifExist != null) {
            return R.error().message("短信已发送，请注意查收！");
        }

        //生成验证码
        String code = RandomUtils.getSixBitRandom();

        //组装短信模板参数
        Map<String, Object> param = new HashMap<>();
        param.put("code", code);

        //发送短信
        smsService.send(mobile, SmsProperties.TEMPLATE_ID, param);

        //将验证码存入redis
        redisTemplate.opsForValue().set("srb:sms:code:" + mobile, code, 2, TimeUnit.MINUTES);

        return R.ok().message("短信发送成功");
    }
}