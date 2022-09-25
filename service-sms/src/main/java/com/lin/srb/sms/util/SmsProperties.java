package com.lin.srb.sms.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter //idea2020.2.3版配置文件自动提示需要这个
@Component
//注意prefix要写到最后一个 "." 符号之前
//调用setter为成员赋值
@ConfigurationProperties(prefix = "ronglianyun.sms")
public class SmsProperties implements InitializingBean {

    /*
    ronglianyun:
        sms:
            server-ip: "app.cloopen.com"
            server-port: "8883"
            account-s-id: "yourcloopenid"
            account-token: "yourcloopentoken"
            app-id: "8aaf070882ede8b3018312f084e2062c"
            template-id: "1"
    */
    //yml中使用横线、或者驼峰的风格，下面都可以匹配。例如"server-ip"、"serverIp"，都可以跟下面的serverIp属性匹配
    private String serverIp;
    private String serverPort;
    private String accountSId;
    private String accountToken;
    private String appId;
    private String templateId;

    public static String SERVER_IP;
    public static String SERVER_PORT;
    public static String ACCOUNT_S_ID;
    public static String ACCOUNT_TOKEN;
    public static String APP_ID;
    public static String TEMPLATE_ID;


    //当私有成员被赋值后，此方法自动被调用，从而初始化常量
    @Override
    public void afterPropertiesSet() throws Exception {
        SERVER_IP = serverIp;
        SERVER_PORT = serverPort;
        ACCOUNT_S_ID = accountSId;
        ACCOUNT_TOKEN = accountToken;
        APP_ID = appId;
        TEMPLATE_ID = templateId;
    }
}