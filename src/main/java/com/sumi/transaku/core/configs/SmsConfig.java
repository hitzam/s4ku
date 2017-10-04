package com.sumi.transaku.core.configs;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsConfig {

    @Value("${sms.masking.url}")
    private String smsMaskingUrl;

    @Value("${sms.masking.userkey}")
    private String smsMaskingUserKey;

    @Value("${sms.masking.passkey}")
    private String smsMaskingPassKey;

	public String getSmsMaskingUrl() {
		return smsMaskingUrl;
	}

	public void setSmsMaskingUrl(String smsMaskingUrl) {
		this.smsMaskingUrl = smsMaskingUrl;
	}

	public String getSmsMaskingUserKey() {
		return smsMaskingUserKey;
	}

	public void setSmsMaskingUserKey(String smsMaskingUserKey) {
		this.smsMaskingUserKey = smsMaskingUserKey;
	}

	public String getSmsMaskingPassKey() {
		return smsMaskingPassKey;
	}

	public void setSmsMaskingPassKey(String smsMaskingPassKey) {
		this.smsMaskingPassKey = smsMaskingPassKey;
	}

    
}