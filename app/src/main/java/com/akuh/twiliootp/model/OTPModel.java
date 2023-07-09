package com.akuh.twiliootp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OTPModel {
        @SerializedName("phone")
        @Expose
        private String phone;
        @SerializedName("client_secret")
        @Expose
        private String clientSecret;
        @SerializedName("sms_message")
        @Expose
        private String smsMessage;

        @SerializedName("success")
        @Expose
        private String success;

        @SerializedName("msg")
        @Expose
        private String msg;


    public OTPModel(String phone, String clientSecret) {
        this.phone = phone;
        this.clientSecret = clientSecret;
    }

    public OTPModel(String phone, String clientSecret, String smsMessage) {
        this.phone = phone;
        this.clientSecret = clientSecret;
        this.smsMessage = smsMessage;
    }

    public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getSmsMessage() {
            return smsMessage;
        }

        public void setSmsMessage(String smsMessage) {
            this.smsMessage = smsMessage;
        }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
