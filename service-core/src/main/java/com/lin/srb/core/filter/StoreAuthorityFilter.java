package com.lin.srb.core.filter;

import com.alibaba.fastjson.JSONObject;
import com.lin.srb.base.util.JwtUtils;
import com.lin.srb.core.pojo.entity.UserInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description：
 * @date： 2022/9/15 23:00
 * @author：gqlin
 */
@Component
public class StoreAuthorityFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        // 得到gateway中新添加的请求头userInfoHeader
        String userInfoHeader = httpServletRequest.getHeader("userInfoHeader");

        if (StringUtils.isNotEmpty(userInfoHeader)) {
            //1.解析userInfoHeader
            JSONObject jsonObject = JSONObject.parseObject(userInfoHeader);
            Long userId = Long.valueOf(jsonObject.getString("userId"));
            String userName = jsonObject.getString("userName");
            Integer userType = Integer.valueOf(jsonObject.getString("userType"));

            //封装用户信息
            UserInfo userInfo = new UserInfo();
            userInfo.setId(userId);
            userInfo.setName(userName);
            userInfo.setUserType(userType);

            String[] authorities = new String[1];
            //0为管理员，其他为用户
            if (userType == 0) {
                authorities[0] = "ROLE_ADMIN";
            } else {
                authorities[0] = "ROLE_USER";
            }

            //2.新建并填充authentication
            UsernamePasswordAuthenticationToken authentication = new
                    UsernamePasswordAuthenticationToken(
                    userInfo, null, AuthorityUtils.createAuthorityList(authorities));
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(
                    httpServletRequest));
            //3.将authentication保存进安全上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
