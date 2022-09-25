package com.lin.srb.sms.service.impl;


import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.lin.common.exception.Assert;
import com.lin.common.exception.BusinessException;
import com.lin.common.result.ResponseEnum;
import com.lin.srb.sms.service.SmsService;
import com.lin.srb.sms.util.SmsProperties;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;


/**
 * @description：
 * @date： 2022/9/6 21:45
 * @author：gqlin
 */
@Service
public class SmsServiceImpl implements SmsService {


    @Override
    public void send(String mobile, String templateId, Map<String, Object> param) {
        //生产环境请求地址：app.cloopen.com
        String serverIp = SmsProperties.SERVER_IP;
        //请求端口
        String serverPort = SmsProperties.SERVER_PORT;

        //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
        String accountSId = SmsProperties.ACCOUNT_S_ID;
        String accountToken = SmsProperties.ACCOUNT_TOKEN;

        //请使用管理控制台中已创建应用的APPID
        String appId = SmsProperties.APP_ID;

        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init(serverIp, serverPort);
        sdk.setAccount(accountSId, accountToken);
        sdk.setAppId(appId);
        sdk.setBodyType(BodyType.Type_JSON);

        //随机生成6位数字验证码
        String code = (String) param.get("code");
        System.out.println("随机生成的6位验证码是： " + code);

        String to = mobile;//发送的目标手机号码

        String[] datas = {code, "2"};//验证码2分钟有效

        //String subAppend="1234";  //可选 扩展码，四位数字 0~9999
        //String reqId="fadfafas";  //可选 第三方自定义消息id，最大支持32位英文数字，同账号下同一自然天内不允许重复
        //HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas);
        //HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas,subAppend,reqId);

        /*HashMap<String, Object> result = sdk.sendTemplateSMS(to, templateId, datas);
        if ("000000".equals(result.get("statusCode"))) {
            //正常返回输出data包体信息（map）
            HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                Object object = data.get(key);
                System.out.println(key + " = " + object);
            }
        } else {
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") + " 错误信息= " + result.get("statusMsg"));
        }*/

        HashMap<String, Object> result = sdk.sendTemplateSMS(to, templateId, datas);
        if (!"000000".equals(result.get("statusCode"))) {
            //由于同意了controller的异常管理，所以这里抛出的异常，会被UnifiedExceptionHandler捕获返回到前端
            throw new BusinessException((String) result.get("statusMsg"), Integer.parseInt((String) result.get("statusCode")));
        }
    }
}
