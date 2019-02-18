package com.github.aaric.achieve.hwsms.entity;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 华为短信消息
 *
 * @author Aaric, created on 2018-01-18T10:56.
 * @since 1.3.16-SNAPSHOT
 */
public class HwSmsMsg {

    /**
     * 模板编码-验证码
     */
    public static final String SMS_TEMPLATE_CODE_VALIDATE = "8819021361625|3a74f416959146469dc3d4f8ad9ac7a8";

    /**
     * 短信模板信息
     */
    public static Map<String, String> SMS_TEMPLATE_INFO;

    static {
        // 添加短信模板信息
        SMS_TEMPLATE_INFO = new HashMap<>();
        // 验证码
        SMS_TEMPLATE_INFO.put(SMS_TEMPLATE_CODE_VALIDATE, "您的验证码为${NUM_6}，两分钟内有效。如非本人操作，请忽略本短信。");
    }

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 模板编号
     */
    private String templateCode;

    /**
     * 模板参数信息列表
     */
    private List<String> templateParams;

    public HwSmsMsg() {
    }

    public HwSmsMsg(String mobile, String templateCode, List<String> templateParams) {
        this.mobile = mobile;
        this.templateCode = templateCode;
        this.templateParams = templateParams;
    }

    /**
     * 获得短信内容
     *
     * @return
     */
    public String getContent() {
        if (null != this.templateCode && null != this.templateParams) {
            String content = SMS_TEMPLATE_INFO.get(this.templateCode);

            // 替换短信变量内容
            if (StringUtils.isNotBlank(content)) {
                int i = 0;
                Pattern p = Pattern.compile("\\$\\{[a-zA-Z_0-9]*\\}");
                Matcher m = p.matcher(content);
                StringBuffer sb = new StringBuffer();

                // 替换字符串
                while (m.find()) {
                    if (i >= templateParams.size()) break;

                    m.appendReplacement(sb, templateParams.get(i++));
                }
                m.appendTail(sb);

                return sb.toString();
            }
        }
        return null;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public List<String> getTemplateParams() {
        return templateParams;
    }

    public void setTemplateParams(List<String> templateParams) {
        this.templateParams = templateParams;
    }
}
