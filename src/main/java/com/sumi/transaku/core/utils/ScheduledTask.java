package com.sumi.transaku.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sumi.transaku.core.configs.GeneralConfig;
import com.sumi.transaku.core.services.SupplierService;

/**
 * 
 * @author Hisyam M. Soldev.
 */


@Component
public class ScheduledTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTask.class);

	@Autowired
	SupplierService supplierService; 

	@Autowired
	GeneralConfig generalConfig; 

	@Autowired
	Utils utils; 
	
//	@Scheduled(cron = "0 4 0 * * ?")  //every 00:04 
	@Scheduled(cron = "0 0/30 * * * ?")  //every 30 minute 
	public void sellerToSupplierConvert() {
		if(generalConfig.getConvertSellerSupplier() == 1){
			System.out.print("-- sellerToSupplierConvert --");
			supplierService.convertSellerToSupplier();
		}
	}
    
}