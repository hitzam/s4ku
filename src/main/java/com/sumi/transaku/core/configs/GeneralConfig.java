package com.sumi.transaku.core.configs;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeneralConfig {

    @Value("${convert.seller.supplier}")
    private int convertSellerSupplier;

	public int getConvertSellerSupplier() {
		return convertSellerSupplier;
	}

}