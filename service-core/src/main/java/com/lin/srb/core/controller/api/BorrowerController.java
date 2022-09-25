package com.lin.srb.core.controller.api;

import com.lin.common.result.R;
import com.lin.srb.core.pojo.entity.UserInfo;
import com.lin.srb.core.pojo.vo.BorrowerVO;
import com.lin.srb.core.service.BorrowerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "借款人")
@RestController
@RequestMapping("/api/core/borrower")
@Slf4j
@PreAuthorize("hasAuthority('ROLE_USER')")
public class BorrowerController {
    @Resource
    private BorrowerService borrowerService;

    @ApiOperation("保存借款人信息")
    @PostMapping("/save")
    public R save(@RequestBody BorrowerVO borrowerVO) {
        //从springsecurity上下文中取出principal，也就是StoreAuthorityFilter中authentication添加的userInfo
        UserInfo user = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();
        borrowerService.saveBorrowerVOByUserId(borrowerVO, userId);
        return R.ok().message("信息提交成功");
    }

    @ApiOperation("获取借款人认证状态")
    @GetMapping("/getBorrowerStatus")
    public R getBorrowerStatus() {
        //从springsecurity上下文中取出principal，也就是StoreAuthorityFilter中authentication添加的userInfo
        UserInfo user = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();
        Integer status = borrowerService.getStatusByUserId(userId);
        return R.ok().data("borrowerStatus", status);
    }
}