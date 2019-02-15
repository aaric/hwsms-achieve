package com.github.aaric.achieve.hwsms.service;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * 华为云发送短信接口
 *
 * @author Aaric, created on 2019-02-15T15:32.
 * @since 0.2.0-SNAPSHOT
 */
public interface HwsmsService {

    /**
     * 发送短信
     *
     * @param templateCode   短信模板编号，格式：“短信通道号|模板ID”
     * @param templateParams 模板中的变量参数列表
     * @param tos            发送人
     * @return 结果信息
     */
    List<HwsmsResult> sendSms(String templateCode, List<String> templateParams, String... tos);

    /**
     * 短信发送结果
     */
    class HwsmsResult {
        private String originTo;
        private String smsMsgId;

        public String getOriginTo() {
            return originTo;
        }

        public void setOriginTo(String originTo) {
            this.originTo = originTo;
        }

        public String getSmsMsgId() {
            return smsMsgId;
        }

        public void setSmsMsgId(String smsMsgId) {
            this.smsMsgId = smsMsgId;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    /**
     * 短信发送状态
     */
    class HwsmsResultStatus {

        public static final String SUCCESS_CODE = "000000";

        private String code;
        private String description;
        private List<HwsmsResult> result;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<HwsmsResult> getResult() {
            return result;
        }

        public void setResult(List<HwsmsResult> result) {
            this.result = result;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }
}
