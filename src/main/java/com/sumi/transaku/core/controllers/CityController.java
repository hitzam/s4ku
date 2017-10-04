package com.sumi.transaku.core.controllers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
/*import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;*/
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sumi.transaku.core.domains.City;
import com.sumi.transaku.core.domains.Province;
import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.services.CityService;
import com.sumi.transaku.core.utils.Utils;

@Controller
@RequestMapping("/city")
public class CityController {
	private static final Logger LOGGER = LoggerFactory.getLogger(CityController.class);
	
	@Autowired
	CityService cityService;

	@Autowired
	Utils utils;
	
	

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  getAllCities() {
		LOGGER.info("getAllCities invoked");
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = cityService.getAllCities();
		
		//entity = new ResponseEntity<Map<String, Object>>(resp, headers, rm.getStatus());
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
//	public String getCity(@PathVariable("id") int id) {
	public ResponseEntity<ResponseModel>  getCity(@PathVariable("id") int id) {
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = cityService.getCity(id);
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/province/{id}", method = RequestMethod.GET)
//	public String getCity(@PathVariable("id") int id) {
	public ResponseEntity<ResponseModel>  getCityByProvince(@PathVariable("id") int id) {
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = cityService.getCityByProvince(new Province(id));
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	

	
	/*@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> add(@RequestBody City city) {
		LOGGER.info("register: "+city.getName());
		ResponseEntity<Map<String, Object>> entity = null;
		String plainPassword = city.getPassword();
		
		city.setPassword(utils.passwordEncoder().encode(city.getPassword()));
		
		ResponseModel rm = cityService.addCity(city);
		
		//City citySaved = (City)rm.getData();
		
		//send email notification
		if(rm!=null && ((boolean)rm.getData())!=false){
			//mail to city
			String namaCity = city.getName();
			String subject = StaticFields.EMAIL_CUSTOMER_REGISTER_SUBJECT;
			String recipient = city.getEmail();
			String content = "";
			content += StaticFields.EMAIL_CUSTOMER_REGISTER_CONTENT.replace("xxxx", city.getEmail());
			content = content.replace("****", plainPassword);
			try {
				mailService.sendMailWithInline(mailConfig.getFrom(), namaCity, subject, recipient, content);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			
			//mail to CS
			//mailService.sendEmail(mailConfig.getFrom(), cityService.getOperatorEmails(), "New City Registration", "\n\nNama City: " + citySaved.getName());
			
		}
		Map<String, Object> resp = new HashMap<String, Object>();
		resp.put("data", rm==null?false:rm.getData());
		resp.put("responseMessage", rm==null?"failed":rm.getResponseMessage());
		entity = new ResponseEntity<Map<String, Object>>(resp, HttpStatus.OK);
		
		return entity;
	}
	
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
