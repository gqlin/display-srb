package com.lin.srb.core.controller.admin;

import com.lin.common.exception.Assert;
import com.lin.common.result.R;
import com.lin.common.result.ResponseEnum;
import com.lin.srb.core.pojo.entity.UserInfo;
import com.lin.srb.core.pojo.vo.LoginVO;
import com.lin.srb.core.pojo.vo.UserInfoVO;
import com.lin.srb.core.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Api(tags = "管理员接口")
@RestController
@RequestMapping("/admin/core/adminInfo")
public class AdminInfoController {

    @Resource
    private UserInfoService userInfoService;

    @ApiOperation("管理员登录")
    @PostMapping("/login")
    public R login(@RequestBody LoginVO loginVO, HttpServletRequest request) {

        //获取登录信息
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();

        //校验登录信息合法性
        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        Assert.notEmpty(password, ResponseEnum.PASSWORD_NULL_ERROR);

        String ip = request.getRemoteAddr();
        UserInfoVO adminInfoVO = userInfoService.login(loginVO, ip);
        return R.ok().data("token", adminInfoVO.getToken());
    }

    //返回信息
    @GetMapping("/getInfo")
    @ApiOperation("获取管理员信息")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public R userinfo() {
        //从springsecurity上下文中取出principal，也就是StoreAuthorityFilter中authentication添加的userInfo
        UserInfo admin = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserInfo adminInfo = userInfoService.getById(admin.getId());
        return R.ok().data("adminInfo", adminInfo);
    }

}
