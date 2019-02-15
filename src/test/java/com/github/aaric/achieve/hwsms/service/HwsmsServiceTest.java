package com.github.aaric.achieve.hwsms.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * HwsmsServiceTest
 *
 * @author Aaric, created on 2019-02-15T15:46.
 * @since 0.2.0-SNAPSHOT
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class HwsmsServiceTest {

    @Autowired
    protected HwsmsService hwsmsService;

    /**
     * 测试手机号
     */
    @Value("${huawei.sms.test.number}")
    private String testNumber;

    @Test
    public void testSendSms() {
        List<HwsmsService.HwsmsResult> result = hwsmsService.sendSms("8819021457459|8b4cb860c1064c4aa7064f01da98168b", Arrays.asList("123456"), testNumber);
        System.out.println(result);
    }
}
