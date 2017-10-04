package com.sumi.transaku.core.controllers;

import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
/*import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;*/
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sumi.transaku.core.configs.MailConfiguration;
import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.domains.Sell;
import com.sumi.transaku.core.services.CustomerService;
import com.sumi.transaku.core.services.MailService;
import com.sumi.transaku.core.services.SellService;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;

@Controller
@RequestMapping("/sell")
public class SellController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SellController.class);
	
	@Autowired
	CustomerService customerService;

	@Autowired
	SellService sellService;

	@Autowired
	MailConfiguration mailConfig;

	@Autowired
	MailService mailService;

	@Autowired
	Utils utils;
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseModel> add(@RequestBody Sell sell) {
		LOGGER.info("sell invoked by: "+sell.getSeller().getId());
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm = sellService.addSellTransaction(sell);
		
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<ResponseModel> update(@RequestBody Sell sell) {
		LOGGER.info("update sell invoked by: "+sell.getSeller().getId());
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm = sellService.updateSell(sell);		
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "/history/{sellerId}/{startDate}/{endDate}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<ResponseModel> history(@PathVariable("sellerId")int sellerId, @PathVariable("startDate")String startDate, @PathVariable("endDate")String endDate) {
		LOGGER.info("history sell invoked by: {} {} - {}", sellerId, startDate, endDate);
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm;
		try {
			rm = sellService.getSellByCustomerAndDateBetween(sellerId, utils.sdhmf.parse(startDate), utils.sdhmf.parse(endDate));
			entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		} catch (ParseException e) {
			rm = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_FAILED, "failed to retrieve data", null);
			entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
			e.printStackTrace();
		}		
		
		return entity;
	}
	
	@RequestMapping(value = "/itemSoldSummary/{sellerId}/{startDate}/{endDate}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<ResponseModel> itemSoldSummary(@PathVariable("sellerId")int sellerId, @PathVariable("startDate")String startDate, @PathVariable("endDate")String endDate) {
		LOGGER.info("item Sold Summary invoked by: {} {} - {}", sellerId, startDate, endDate);
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm;
		try {
			rm = sellService.getItemSoldByCustomerAndDateBetween(sellerId, utils.sdhmf.parse(startDate), utils.sdhmf.parse(endDate));
			entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		} catch (ParseException e) {
			rm = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_FAILED, "failed to retrieve data", null);
			entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
			e.printStackTrace();
		}		
		
		return entity;
	}
	
	
}
