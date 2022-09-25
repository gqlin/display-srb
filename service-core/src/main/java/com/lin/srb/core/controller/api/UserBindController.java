package com.lin.srb.core.controller.api;

import com.alibaba.fastjson.JSON;
import com.lin.common.result.R;
import com.lin.srb.core.hfb.RequestHelper;
import com.lin.srb.core.pojo.entity.UserInfo;
import com.lin.srb.core.pojo.vo.UserBindVO;
import com.lin.srb.core.service.UserBindService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "会员账号绑定")
@RestController
@RequestMapping("/api/core/userBind")
@Slf4j
public class UserBindController {
    @Resource
    private UserBindService userBindService;

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @ApiOperation("账户绑定提交数据")
    @PostMapping("/bind")//所有需要登录才能执行的功能，我们都写到auth路径下，后期我们补充网关的校验
    public R bind(@RequestBody UserBindVO userBindVO) {
        //从springsecurity上下文中取出principal，也就是StoreAuthorityFilter中authentication添加的userInfo
        UserInfo user = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        //根据userId做账户绑定
        String formStr = userBindService.commitBindUser(userBindVO, userId);
        return R.ok().data("formStr", formStr);
    }

    @ApiOperation("账户绑定异步回调")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        log.info("用户账号绑定异步回调：" + JSON.toJSONString(paramMap));
        //校验签名
        if (!RequestHelper.isSignEquals(paramMap)) {
            log.error("用户账号绑定异步回调签名错误：" + JSON.toJSONString(paramMap));
            return "fail";
        }
        //修改绑定状态
        userBindService.notify(paramMap);
        return "success";
    }
}