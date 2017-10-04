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
import com.sumi.transaku.core.domains.SellerCapital;
import com.sumi.transaku.core.repositories.CustomerRepository;
import com.sumi.transaku.core.services.CustomerService;
import com.sumi.transaku.core.services.MailService;
import com.sumi.transaku.core.services.SellerCapitalService;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;

@Controller
@RequestMapping("/capital")
public class SellerCapitalController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SellerCapitalController.class);
	
	@Autowired
	CustomerService customerService;
	
//	@Autowired
//	CustomerRepository customerRepository
	
	@Autowired
	SellerCapitalService capitalService;

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	MailConfiguration mailConfig;

	@Autowired
	MailService mailService;

	@Autowired
	Utils utils;
	
	
	@RequestMapping(value = "/init", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseModel> add(@RequestBody SellerCapital capital) {
		//Customer seller = customerRepository.findOne(capital.getSeller().getId());
		LOGGER.info("init capital for: {}", capital.getSeller().getId());
		ResponseEntity<ResponseModel> entity = null;
		/*int principalId = ((Customer)customerService.getCustomerByEmail(principal.getName()).getData()).getId();
		
		if(capital.getSeller().getId() == principalId){*/
			//capital.setSeller(seller);
			ResponseModel rm = capitalService.initCapital(capital);
			entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		/*}else{
			ResponseModel rm = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_UNAUTHORIZED, "Unauthorized to update profile", null);
			entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		}*/
		return entity;
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<ResponseModel> update(@RequestBody SellerCapital capital) {
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm = capitalService.updateCapital(capital);
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "/reportByDateBetween/{sellerId}/{startDate}/{endDate}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<ResponseModel> reportByDateBetween(@PathVariable("sellerId")String sellerId, 
			@PathVariable("startDate")String startDate,@PathVariable("endDate")String endDate) {
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm;
		try {
			rm = capitalService.getReportByTrxDateBetween(Integer.parseInt(sellerId), utils.sdtf.parse(startDate+":01"), utils.sdtf.parse(endDate+":01"));
			entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		} catch (NumberFormatException | ParseException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			rm = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_FAILED, "invalid date format", null);
		}
		
		return entity;
	}
	
	
}
