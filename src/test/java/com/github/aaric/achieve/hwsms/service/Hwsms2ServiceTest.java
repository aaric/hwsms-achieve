package com.github.aaric.achieve.hwsms.service;

import com.github.aaric.achieve.hwsms.entity.SmsMsg;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * Hwsms2ServiceTest
 *
 * @author Aaric, created on 2019-04-09T17:24.
 * @since 0.3.0-SNAPSHOT
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class Hwsms2ServiceTest {

    @Autowired
    protected Hwsms2Service hwsms2Service;

    /**
     * 测试手机号
     */
    @Value("${huawei.sms.test.number}")
    private String testNumber;

    @Test
    @Ignore
    public void testSendSms() {
        Map<String, String> templateParams = new HashMap<>();
        templateParams.put("code", "123456");
        //Assert.assertNotNull(hwsms2Service.sendSms(new SmsMsg(testNumber, SmsMsg.SMS_TEMPLATE_CODE_TEST, templateParams), testNumber));
        Assert.assertNotNull(hwsms2Service.sendSms(new SmsMsg(testNumber, SmsMsg.SMS_TEMPLATE_CODE_VALIDATE, templateParams), testNumber));
    }
}
