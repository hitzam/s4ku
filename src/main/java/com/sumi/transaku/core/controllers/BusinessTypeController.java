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

import com.sumi.transaku.core.domains.BusinessType;
import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.services.BusinessTypeService;
import com.sumi.transaku.core.utils.Utils;

@Controller
@RequestMapping("/businessType")
public class BusinessTypeController {
	private static final Logger LOGGER = LoggerFactory.getLogger(BusinessTypeController.class);
	
	@Autowired
	BusinessTypeService businessTypeService;

	@Autowired
	Utils utils;
	
	

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  getAllBusinessTypes() {
		LOGGER.info("getAllBusinessTypes invoked");
		
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm = businessTypeService.getAllBusinessTypes();
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  getBusinessType(@PathVariable("id") int id) {
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = businessTypeService.getBusinessType(id);
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	

	
	/*@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> add(@RequestBody BusinessType businessType) {
		LOGGER.info("register: "+businessType.getName());
		ResponseEntity<Map<String, Object>> entity = null;
		String plainPassword = businessType.getPassword();
		
		businessType.setPassword(utils.passwordEncoder().encode(businessType.getPassword()));
		
		ResponseModel rm = businessTypeService.addBusinessType(businessType);
		
		//BusinessType businessTypeSaved = (BusinessType)rm.getData();
		
		//send email notification
		if(rm!=null && ((boolean)rm.getData())!=false){
			//mail to businessType
			String namaBusinessType = businessType.getName();
			String subject = StaticFields.EMAIL_CUSTOMER_REGISTER_SUBJECT;
			String recipient = businessType.getEmail();
			String content = "";
			content += StaticFields.EMAIL_CUSTOMER_REGISTER_CONTENT.replace("xxxx", businessType.getEmail());
			content = content.replace("****", plainPassword);
			try {
				mailService.sendMailWithInline(mailConfig.getFrom(), namaBusinessType, subject, recipient, content);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			
			//mail to CS
			//mailService.sendEmail(mailConfig.getFrom(), businessTypeService.getOperatorEmails(), "New BusinessType Registration", "\n\nNama BusinessType: " + businessTypeSaved.getName());
			
		}
		Map<String, Object> resp = new HashMap<String, Object>();
		resp.put("data", rm==null?false:rm.getData());
		resp.put("responseMessage", rm==null?"failed":rm.getResponseMessage());
		entity = new ResponseEntity<Map<String, Object>>(resp, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> update(@RequestBody BusinessType businessType) {
		ResponseEntity<Map<String, Object>> entity = null;
		
		ResponseModel rm = businessTypeService.updateBusinessType(businessType);		
		LOGGER.info("updated businessType{}", rm.getData().toString());
		Map<String, Object> resp = new HashMap<String, Object>();
		resp.put("data", rm.getData());
		resp.put("responseMessage", rm.getResponseMessage());
		entity = new ResponseEntity<Map<String, Object>>(resp, HttpStatus.OK);
		
		return entity;
	}
	
	
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> delete(@RequestBody BusinessType businessType) {
		ResponseEntity<Map<String, Object>> entity = null;
		
		ResponseModel rmBusinessType = businessTypeService.getBusinessType(businessType.getId());
		BusinessType businessTypeToDelete = (BusinessType)rmBusinessType.getData();
		businessTypeToDelete.setEnabled(StaticFields.STATUS_DISABLED);
		
		//ResponseModel rm = businessTypeService.updateBusinessType(businessTypeToDelete);		
		ResponseModel rm = businessTypeService.deleteBusinessType(businessTypeToDelete);		
		
		Map<String, Object> resp = new HashMap<String, Object>();
		resp.put("data", rm.getData());
		resp.put("responseMessage", rm.getResponseMessage());
		entity = new ResponseEntity<Map<String, Object>>(resp, HttpStatus.OK);
		
		return entity;
	}*/
	
		
}
