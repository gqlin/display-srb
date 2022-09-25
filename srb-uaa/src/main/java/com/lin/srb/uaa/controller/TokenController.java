package com.lin.srb.uaa.controller;

import com.lin.srb.base.util.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description：
 * @date： 2022/9/14 18:26
 * @author：gqlin
 */
@Api(tags = "token管理")
@Slf4j
@RestController
@RequestMapping("/uaa")
public class TokenController {

    @ApiOperation("创建token")
    @GetMapping("/createToken/{userId}/{userName}/{userType}")
    public String createToken(@ApiParam(required = true, value = "用户id") @PathVariable Long userId,@ApiParam(required = true, value = "用户名")@PathVariable String userName,@ApiParam(required = true, value = "用户类型")@PathVariable Integer userType ) {
        String token = JwtUtils.createToken(userId, userName,userType);
        return token;
    }
}
