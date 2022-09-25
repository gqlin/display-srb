package com.lin.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lin.srb.core.pojo.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lin.srb.core.pojo.entity.UserLoginRecord;
import com.lin.srb.core.pojo.query.UserInfoQuery;
import com.lin.srb.core.pojo.vo.LoginVO;
import com.lin.srb.core.pojo.vo.RegisterVO;
import com.lin.srb.core.pojo.vo.UserIndexVO;
import com.lin.srb.core.pojo.vo.UserInfoVO;

import java.util.List;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author Helen
 * @since 2022-09-03
 */
public interface UserInfoService extends IService<UserInfo> {
    void register(RegisterVO registerVO);

    UserInfoVO login(LoginVO loginVO, String ip);

    IPage<UserInfo> listPage(Page<UserInfo> pageParam, UserInfoQuery userInfoQuery);

    void lock(Long id, Integer status);

    boolean checkMobile(String mobile, Integer userType);

    UserIndexVO getIndexUserInfo(Long userId);

    String getMobileByBindCode(String bindCode);
}
