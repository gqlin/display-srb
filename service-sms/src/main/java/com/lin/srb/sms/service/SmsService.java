package com.lin.srb.sms.service;

import java.util.Map;

public interface SmsService {
    //templateId:模板id，因为我们可能是登录需要验证码，投标需要验证码，需要不同的验证码模板
    void send(String mobile, String templateId,Map<String,Object> param);
}