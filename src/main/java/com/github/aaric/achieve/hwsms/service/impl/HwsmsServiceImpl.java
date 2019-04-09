package com.github.aaric.achieve.hwsms.service.impl;

import com.github.aaric.achieve.hwsms.service.HwsmsService;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 华为云发送短信接口实现
 *
 * @author Aaric, created on 2019-02-15T15:37.
 * @since 0.2.0-SNAPSHOT
 */
@Service
public class HwsmsServiceImpl implements HwsmsService {

    /**
     * 无需修改,用于格式化鉴权头域,给"X-WSSE"参数赋值
     */
    private static final String WSSE_HEADER_FORMAT = "UsernameToken Username=\"%s\",PasswordDigest=\"%s\",Nonce=\"%s\",Created=\"%s\"";

    /**
     * 无需修改,用于格式化鉴权头域,给"Authorization"参数赋值
     */
    private static final String AUTH_HEADER_VALUE = "WSSE realm=\"SDP\",profile=\"UsernameToken\",type=\"Appkey\"";

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

    /**
     * 短信状态回调地址，默认“”
     */
    @Value("${huawei.sms.callBackUrl}")
    private String callBackUrl;

    @Override
    public List<HwsmsResult> sendSms(String templateCode, List<String> templateParams, String... tos) {
        // APP接入地址：必填,请参考"开发准备"获取如下数据,替换为实际值
        String url = apiHost + "/sms/batchSendSms/v1";

        // 解析短信通道号和模板ID
        if (StringUtils.isBlank(templateCode)) {
            System.out.println("templateCode is blank.");
            return null;
        }
        String[] senderTemplateIds = templateCode.split("\\|");
        if (null == senderTemplateIds || 2 != senderTemplateIds.length) {
            System.out.println("templateCode is error.");
            return null;
        }

        // 短信通道号：国内短信签名通道号或国际/港澳台短信通道号
        String sender = senderTemplateIds[0];

        // 模板ID
        String templateId = senderTemplateIds[1];

        // 模板变量信息
        if (null == templateParams || 0 == templateParams.size()) {
            System.out.println("templateParams is null.");
            return null;
        }
        Gson gson = new Gson();
        String templateParas = gson.toJson(templateParams);

        // 签名名称：国际/港澳台短信不用关注该参数
        String signature = "广汽三菱";

        // 短信接收人号码：必填,全局号码格式(包含国家码),示例:+8613400000000,多个号码之间用英文逗号分隔
        String receiver = getFormatTosString(tos);

        // 客户的回调地址：选填,短信状态报告接收地址,推荐使用域名,为空或者不填表示不接收状态报告
        String statusCallBackUrl = callBackUrl;

        // 请求Body：不携带签名名称时,signature请填null
        String body = buildRequestBody(sender, receiver, templateId, templateParas, statusCallBackUrl, signature);
        if (null == body || body.isEmpty()) {
            System.out.println("body is null.");
            return null;
        }

        // 请求Headers中的X-WSSE参数值
        String wsseHeader = buildWsseHeader(accessKey, accessSecretKey);
        if (null == wsseHeader || wsseHeader.isEmpty()) {
            System.out.println("wsse header is null.");
            return null;
        }

        try {
            // 设置超时时间(5秒默认值)
            RequestConfig config = RequestConfig.custom()
                    .setSocketTimeout(5000)
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .build();

            // 为防止因HTTPS证书认证失败造成API调用失败,需要先忽略证书信任问题
            CloseableHttpClient client = HttpClients.custom()
                    .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null,
                            (x509CertChain, authType) -> true).build())
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .setDefaultRequestConfig(config)
                    .build();

            // 执行发送短信调用
            HttpResponse response = client.execute(RequestBuilder.create("POST")//请求方法POST
                    .setUri(url)
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .addHeader(HttpHeaders.AUTHORIZATION, AUTH_HEADER_VALUE)
                    .addHeader("X-WSSE", wsseHeader)
                    .setEntity(new StringEntity(body)).build());

            // 打印响应头域信息
            if (null != response) {
                // 获得返回信息
                String result = EntityUtils.toString(response.getEntity());
                //System.out.println(result);
                if (StringUtils.isNotBlank(result)) {
                    HwsmsResultStatus status = gson.fromJson(result, HwsmsResultStatus.class);
                    if (null != status && HwsmsResultStatus.SUCCESS_CODE.equals(status.getCode())) {
                        return status.getResult();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 短信接收人号码：必填,全局号码格式(包含国家码),示例:+8613400000000,多个号码之间用英文逗号分隔
     *
     * @param tos 发送人
     * @return
     */
    private String getFormatTosString(String... tos) {
        if (null != tos && 0 != tos.length) {
            for (int i = 0; i < tos.length; i++) {
                tos[i] = "+86" + tos[i];
            }
            return StringUtils.join(tos, ",");
        }
        return null;
    }

    /**
     * 构造请求Body体
     *
     * @param sender            短信通道号
     * @param receiver          短信接收人号码
     * @param templateId        模板ID
     * @param templateParas     模板变量信息
     * @param statusCallbackUrl 客户的回调地址
     * @param signature         签名名称,使用国内短信通用模板时填写
     * @return
     */
    private String buildRequestBody(String sender, String receiver, String templateId, String templateParas,
                                    String statusCallbackUrl, String signature) {
        if (null == sender || null == receiver || null == templateId || sender.isEmpty() || receiver.isEmpty()
                || templateId.isEmpty()) {
            System.out.println("buildRequestBody(): sender, receiver or templateId is null.");
            return null;
        }
        List<NameValuePair> keyValues = new ArrayList<NameValuePair>();

        keyValues.add(new BasicNameValuePair("from", sender));
        keyValues.add(new BasicNameValuePair("to", receiver));
        keyValues.add(new BasicNameValuePair("templateId", templateId));
        if (null != templateParas && !templateParas.isEmpty()) {
            keyValues.add(new BasicNameValuePair("templateParas", templateParas));
        }
        if (null != statusCallbackUrl && !statusCallbackUrl.isEmpty()) {
            keyValues.add(new BasicNameValuePair("statusCallback", statusCallbackUrl));
        }
        if (null != signature && !signature.isEmpty()) {
            keyValues.add(new BasicNameValuePair("signature", signature));
        }

        return URLEncodedUtils.format(keyValues, Charset.forName("UTF-8"));
    }

    /**
     * 构造X-WSSE参数值
     *
     * @param appKey    授权访问密钥（AK）
     * @param appSecret 授权访问密钥（SK）
     * @return
     */
    private String buildWsseHeader(String appKey, String appSecret) {
        if (null == appKey || null == appSecret || appKey.isEmpty() || appSecret.isEmpty()) {
            System.out.println("buildWsseHeader(): appKey or appSecret is null.");
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String time = sdf.format(new Date()); //Created
        String nonce = UUID.randomUUID().toString().replace("-", ""); //Nonce

        byte[] passwordDigest = DigestUtils.sha256(nonce + time + appSecret);
        String hexDigest = Hex.encodeHexString(passwordDigest);

        // 如果JDK版本是1.8,请加载原生Base64类,并使用如下代码
        String passwordDigestBase64Str = Base64.getEncoder().encodeToString(hexDigest.getBytes());

        return String.format(WSSE_HEADER_FORMAT, appKey, passwordDigestBase64Str, nonce, time);
    }
}
