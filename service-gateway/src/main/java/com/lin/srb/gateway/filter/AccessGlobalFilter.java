package com.lin.srb.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.lin.srb.base.util.JwtUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * @description：
 * @date： 2022/9/15 10:15
 * @author：gqlin
 */
@Order(-100)
@Component
public class AccessGlobalFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("进入网关全局过滤器；");
        // 请求参数
        ServerHttpRequest request = exchange.getRequest();
        // 获取请求路径
        String path = request.getURI().getPath();

        // 以下请求不需要令牌直接放行
        // login放行
        if ("/admin/core/adminInfo/login".equals(path) || "/api/core/userInfo/login".equals(path)) {
            // 放行
            return chain.filter(exchange);
        }
        // 注册放行
        if (path.equals("/api/core/userInfo/register")) {
            return chain.filter(exchange);
        }
        // 短信发送放行
        if (path.indexOf("/api/sms/send") != -1) {
            return chain.filter(exchange);
        }
        // 文件功能放行
        if (path.indexOf("/api/oss/file") != -1) {
            return chain.filter(exchange);
        }
        // 回调接口放行
        if (path.indexOf("/api/core/") != -1) {
            if (path.indexOf("/notify") != -1) {
                return chain.filter(exchange);
            }
        }
        System.out.println("开始校验token合法性");

        //合法的jwt令牌放行
        String token = request.getHeaders().getFirst("token");
        if (JwtUtils.checkToken(token)) {
            System.out.println("token合法性校验通过");

            //根据token获取用户信息
            Long userId = JwtUtils.getUserId(token);
            String userName = JwtUtils.getUserName(token);
            Integer userType = JwtUtils.getUserType(token);
            HashMap<String, Object> userInfoParams = new HashMap<>();
            userInfoParams.put("userId", userId);
            userInfoParams.put("userName", userName);
            userInfoParams.put("userType", userType);
            String userInfoJson = JSON.toJSONString(userInfoParams);

            // 开始组装一个新的请求头
            // 用户信息存储在名为userInfoHeader的header中
            ServerHttpRequest userInfoHeader = request.mutate().header("userInfoHeader", userInfoJson).build();
            // 重置请求
            exchange.mutate().request(userInfoHeader);

            // 放行
            return chain.filter(exchange);
        }
        // 4.拦截
        // 4.1.禁止访问，设置状态码
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        // 4.2.结束处理
        return exchange.getResponse().setComplete();
    }
}
