package com.sumi.transaku.core.controllers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

import com.sumi.transaku.core.domains.City;
import com.sumi.transaku.core.domains.Customer;
import com.sumi.transaku.core.domains.Province;
import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.repositories.CustomerRepository;
import com.sumi.transaku.core.services.CityService;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;

@Controller
@RequestMapping("/login")
public class LoginController {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	Utils utils;
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseModel> add(@RequestBody Customer customer) {
		LOGGER.info("login: "+customer.getName());
		ResponseModel rm = null;		
		ResponseEntity<ResponseModel> entity = null;
		
		Customer registeredCust = customerRepository.findByEmail(customer.getEmail());
		if(registeredCust!=null){
			if(utils.passwordEncoder().matches(customer.getPassword(), registeredCust.getPassword()))
				rm = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", null);
			else
				rm = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "email & password do not match", null);
		}else{
			rm = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "account not registered", null);
		}
		
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	/*
	@RequestMapping(value = "", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> update(@RequestBody City city) {
		ResponseEntity<Map<String, Object>> entity = null;
		
		ResponseModel rm = cityService.updateCity(city);		
		LOGGER.info("updated city{}", rm.getData().toString());
		Map<String, Object> resp = new HashMap<String, Object>();
		resp.put("data", rm.getData());
		resp.put("responseMessage", rm.getResponseMessage());
		entity = new ResponseEntity<Map<String, Object>>(resp, HttpStatus.OK);
		
		return entity;
	}
	
	
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> delete(@RequestBody City city) {
		ResponseEntity<Map<String, Object>> entity = null;
		
		ResponseModel rmCity = cityService.getCity(city.getId());
		City cityToDelete = (City)rmCity.getData();
		cityToDelete.setEnabled(StaticFields.STATUS_DISABLED);
		
		//ResponseModel rm = cityService.updateCity(cityToDelete);		
		ResponseModel rm = cityService.deleteCity(cityToDelete);		
		
		Map<String, Object> resp = new HashMap<String, Object>();
		resp.put("data", rm.getData());
		resp.put("responseMessage", rm.getResponseMessage());
		entity = new ResponseEntity<Map<String, Object>>(resp, HttpStatus.OK);
		
		return entity;
	}*/
	
		
}
