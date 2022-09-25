package com.lin.srb.core.controller.api;

import com.alibaba.fastjson.JSON;
import com.lin.common.result.R;
import com.lin.srb.core.hfb.RequestHelper;
import com.lin.srb.core.pojo.entity.LendItem;
import com.lin.srb.core.pojo.entity.UserInfo;
import com.lin.srb.core.pojo.vo.InvestVO;
import com.lin.srb.core.service.LendItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Api(tags = "标的的投资")
@RestController
@RequestMapping("/api/core/lendItem")
@Slf4j
public class LendItemController {

    @Resource
    LendItemService lendItemService;
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @ApiOperation("会员投资提交数据")
    @PostMapping("/commitInvest")
    public R commitInvest(@RequestBody InvestVO investVO) {
        //从springsecurity上下文中取出principal，也就是StoreAuthorityFilter中authentication添加的userInfo
        UserInfo user = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();
        String userName = user.getName();
        investVO.setInvestUserId(userId);
        investVO.setInvestName(userName);
        //构建充值自动提交表单
        String formStr = lendItemService.commitInvest(investVO);
        return R.ok().data("formStr", formStr);
    }

    @ApiOperation("会员投资异步回调")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        log.info("用户投资异步回调：" + JSON.toJSONString(paramMap));
        //校验签名 P2pInvestNotifyVo
        if (RequestHelper.isSignEquals(paramMap)) {
            if ("0001".equals(paramMap.get("resultCode"))) {
                lendItemService.notify(paramMap);
            } else {
                log.info("用户投资异步回调失败：" + JSON.toJSONString(paramMap));
                return "fail";
            }
        } else {
            log.info("用户投资异步回调签名错误：" + JSON.toJSONString(paramMap));
            return "fail";
        }
        return "success";
    }

    @ApiOperation("获取列表")
    @GetMapping("/list/{lendId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public R list(
            @ApiParam(value = "标的id", required = true)
            @PathVariable Long lendId) {
        List<LendItem> list = lendItemService.selectByLendId(lendId);
        return R.ok().data("list", list);
    }
}