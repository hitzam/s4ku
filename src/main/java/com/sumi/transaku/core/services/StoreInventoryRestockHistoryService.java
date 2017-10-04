package com.sumi.transaku.core.services;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.domains.StoreInventoryRestockHistory;
import com.sumi.transaku.core.repositories.CustomerRepository;
import com.sumi.transaku.core.repositories.StoreInventoryRestockHistoryRepository;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;


@Service
public class StoreInventoryRestockHistoryService {
	private static final Logger LOGGER = LoggerFactory.getLogger(StoreInventoryRestockHistoryService.class);
	
	@Autowired
	StoreInventoryRestockHistoryRepository historyRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	SellerCapitalService capitalService;

	@Autowired
	Utils utils;
	
	/*public ResponseModel addHistory(StoreInventoryRestockHistory history) {
		LOGGER.info("add history");
		ResponseModel model = null;
		history.setCreatedDate(new Date());
		StoreInventoryRestockHistory historySaved = historyRepository.save(history);
		System.out.println(historySaved);        
		
		if(historySaved != null){
			LOGGER.info(historySaved.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", historySaved.getId());
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to add histpry", null);
		}
		
		return model;
	}*/
	
	
	public void addHistory(StoreInventoryRestockHistory history) {
		LOGGER.info("add history");
		history.setCreatedDate(new Date());
		StoreInventoryRestockHistory historySaved = historyRepository.save(history);
	}
	
	public List<StoreInventoryRestockHistory> getHistoryBySeller(int sellerId) {
		LOGGER.info("get history by seller");
		return historyRepository.findBySellerId(sellerId);
	}
	
	public List<StoreInventoryRestockHistory> getHistoryBySellerAndDate(int sellerId, Date startDate, Date endDate) {
		LOGGER.info("get history by seller and date");
		return historyRepository.findBySellerIdAndCreatedDateBetween(sellerId, startDate, endDate);
	}
	
	
}
