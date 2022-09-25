package com.lin.srb.sms;

import com.lin.srb.sms.util.SmsProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UtilsTests {
    @Test
    public void testProperties(){
        System.out.println(SmsProperties.ACCOUNT_S_ID);
        System.out.println(SmsProperties.ACCOUNT_TOKEN);
        System.out.println(SmsProperties.SERVER_PORT);
        System.out.println(SmsProperties.SERVER_IP);
        System.out.println(SmsProperties.TEMPLATE_ID);
        System.out.println(SmsProperties.APP_ID);
    }
}