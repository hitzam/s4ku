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

import com.sumi.transaku.core.domains.Province;
import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.services.ProvinceService;
import com.sumi.transaku.core.utils.Utils;

@Controller
@RequestMapping("/province")
public class ProvinceController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProvinceController.class);
	
	@Autowired
	ProvinceService provinceService;

	@Autowired
	Utils utils;
	
	

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  getAllProvinces() {
		LOGGER.info("getAllProvinces invoked");
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = provinceService.getAllProvinces();
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
//	public String getProvince(@PathVariable("id") int id) {
	public ResponseEntity<ResponseModel>  getProvince(@PathVariable("id") int id) {
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = provinceService.getProvince(id);
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	

	
	/*@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseModel> add(@RequestBody Province province) {
		LOGGER.info("register: "+province.getName());
		ResponseEntity<ResponseModel> entity = null;
		String plainPassword = province.getPassword();
		
		province.setPassword(utils.passwordEncoder().encode(province.getPassword()));
		
		ResponseModel rm = provinceService.addProvince(province);
		
		//Province provinceSaved = (Province)rm.getData();
		
		//send email notification
		if(rm!=null && ((boolean)rm.getData())!=false){
			//mail to province
			String namaProvince = province.getName();
			String subject = StaticFields.EMAIL_CUSTOMER_REGISTER_SUBJECT;
			String recipient = province.getEmail();
			String content = "";
			content += StaticFields.EMAIL_CUSTOMER_REGISTER_CONTENT.replace("xxxx", province.getEmail());
			content = content.replace("****", plainPassword);
			try {
				mailService.sendMailWithInline(mailConfig.getFrom(), namaProvince, subject, recipient, content);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			
			//mail to CS
			//mailService.sendEmail(mailConfig.getFrom(), provinceService.getOperatorEmails(), "New Province Registration", "\n\nNama Province: " + provinceSaved.getName());
			
		}
		ResponseModel resp = new HashResponseModel();
		resp.put("data", rm==null?false:rm.getData());
		resp.put("responseMessage", rm==null?"failed":rm.getResponseMessage());
		entity = new ResponseEntity<ResponseModel>(resp, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<ResponseModel> update(@RequestBody Province province) {
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm = provinceService.updateProvince(province);		
		LOGGER.info("updated province{}", rm.getData().toString());
		ResponseModel resp = new HashResponseModel();
		resp.put("data", rm.getData());
		resp.put("responseMessage", rm.getResponseMessage());
		entity = new ResponseEntity<ResponseModel>(resp, HttpStatus.OK);
		
		return entity;
	}
	
	
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<ResponseModel> delete(@RequestBody Province province) {
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rmProvince = provinceService.getProvince(province.getId());
		Province provinceToDelete = (Province)rmProvince.getData();
		provinceToDelete.setEnabled(StaticFields.STATUS_DISABLED);
		
		//ResponseModel rm = provinceService.updateProvince(provinceToDelete);		
		ResponseModel rm = provinceService.deleteProvince(provinceToDelete);		
		
		ResponseModel resp = new HashResponseModel();
		resp.put("data", rm.getData());
		resp.put("responseMessage", rm.getResponseMessage());
		entity = new ResponseEntity<ResponseModel>(resp, HttpStatus.OK);
		
		return entity;
	}*/
	
		
}
