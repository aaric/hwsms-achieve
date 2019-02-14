package com.github.aaric.achieve.hwsms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * SmsTest
 *
 * @author Aaric, created on 2019-02-14T14:55.
 * @since 0.0.1-SNAPSHOT
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SmsTest {

    /**
     * APP接入地址
     */
    @Value("${huawei.sms.apiHost}")
    private String apiHost;

    /**
     * 授权访问密钥（AK）
     */
    @Value("${huawei.sms.accessKey}")
    private String accessKey;

    /**
     * 授权访问密钥（SK）
     */
    @Value("${huawei.sms.accessSecretKey}")
    private String accessSecretKey;

    @Test
    public void testShowAccountInfo() {
        System.out.println(apiHost);
        System.out.println(accessKey);
        System.out.println(accessSecretKey);
    }
}
