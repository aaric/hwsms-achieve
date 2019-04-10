package com.github.aaric.achieve.hwsms.service;

import com.github.aaric.achieve.hwsms.entity.SmsMsg;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * 华为云发送短信接口（免模板）
 *
 * @author Aaric, created on 2019-04-09T17:11.
 * @since 0.3.0-SNAPSHOT
 */
public interface Hwsms2Service {

    /**
     * 发送短信
     *
     * @param smsMsg 短信实体
     * @param tos    发送人
     * @return 结果信息
     */
    List<HwsmsResult> sendSms(SmsMsg smsMsg, String... tos);

    /**
     * 短信发送结果
     */
    class HwsmsResult {
        private String originTo;
        private String from;
        private String smsMsgId;

        public String getOriginTo() {
            return originTo;
        }

        public void setOriginTo(String originTo) {
            this.originTo = originTo;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
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
