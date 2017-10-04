package com.sumi.transaku.core.configs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.sumi.transaku.core.domains.Customer;
import com.sumi.transaku.core.utils.StaticFields;

public class CustomTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Customer customer = (Customer) authentication.getPrincipal();
        final Map<String, Object> additionalInfo = new HashMap<String, Object>();

        additionalInfo.put("success", true);
        /*if(customer.getStatus() == StaticFields.STATUS_CUSTOMER_REGISTERED){
        	customer = new Customer();
        	customer.setEnabled(0);
        	customer.setStatus(StaticFields.STATUS_CUSTOMER_REGISTERED);
        }*/
        additionalInfo.put("profile", customer);
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);

        return accessToken;
    }

	/*@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken arg0, OAuth2Authentication arg1) {
		// TODO Auto-generated method stub
		return null;
	}*/

}