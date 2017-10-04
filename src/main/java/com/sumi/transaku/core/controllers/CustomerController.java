package com.sumi.transaku.core.controllers;

import java.nio.file.Paths;
import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.sumi.transaku.core.configs.WebSecurityConfiguration;
import com.sumi.transaku.core.domains.Customer;
import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.services.CustomerService;
import com.sumi.transaku.core.services.MailService;
import com.sumi.transaku.core.utils.AwsS3Util;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;

@Controller
@RequestMapping("/customer")
public class CustomerController {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);
	
	@Autowired
	WebSecurityConfiguration security;

	@Autowired
	CustomerService customerService;

	@Autowired
	MailConfiguration mailConfig;

	@Autowired
	MailService mailService;

	@Autowired
	Utils utils;

	
	private final ResourceLoader resourceLoader;
	@Autowired
	public CustomerController(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseModel> add(@RequestBody Customer customer) {
		LOGGER.info("register: "+customer.getName());
		ResponseEntity<ResponseModel> entity = null;
		String plainPassword = customer.getPassword();
		
		customer.setPassword(utils.passwordEncoder().encode(customer.getPassword()));
		
		if(customer.getPhone().startsWith("0"))
			customer.setPhone(customer.getPhone().replaceFirst("0", "62"));
		
		ResponseModel rm = customerService.addCustomer(customer);
		
		//send email notification
		if(rm!=null){
			//mail to customer
			String namaCustomer = customer.getName();
			String subject = StaticFields.EMAIL_CUSTOMER_REGISTER_SUBJECT;
			String recipient = customer.getEmail();
			String content = "";
			content += StaticFields.SMS_CUSTOMER_REGISTER_CONTENT.replace("xxxx", customer.getEmail());
			content = content.replace("****", plainPassword);
			/*try {
				mailService.sendMailWithInline(mailConfig.getFrom(), namaCustomer, subject, recipient, content);
			} catch (MessagingException e) {
				e.printStackTrace();
			}*/
			
			//mail to CS
			//mailService.sendEmail(mailConfig.getFrom(), customerService.getOperatorEmails(), "New Customer Registration", "\n\nNama Customer: " + customerSaved.getName());
			
		}
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<ResponseModel> update(@RequestBody Customer customer, Principal principal) {
		ResponseEntity<ResponseModel> entity = null;
		
		int principalId = ((Customer)customerService.getCustomerByEmail(principal.getName()).getData()).getId();
		
		if(customer.getId() == principalId){
			if(customer.getPhone().startsWith("0"))
				customer.setPhone(customer.getPhone().replaceFirst("0", "62"));
			
			ResponseModel rm = customerService.updateCustomer(customer);		
			entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		}else{
			ResponseModel rm = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_UNAUTHORIZED, "Unauthorized to update profile", null);
			entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		}
		
		return entity;
	}
	
	
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<ResponseModel> delete(@RequestBody Customer customer) {
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rmCustomer = customerService.getCustomer(customer.getId());
		Customer customerToDelete = (Customer)rmCustomer.getData();
		customerToDelete.setEnabled(StaticFields.STATUS_DISABLED);
		
		//ResponseModel rm = customerService.updateCustomer(customerToDelete);		
		ResponseModel rm = customerService.deleteCustomer(customerToDelete);		
		
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<ResponseModel> changePassword(@RequestBody Customer customer, Principal principal) {
		ResponseEntity<ResponseModel> entity = null;
		
		int principalId = ((Customer)customerService.getCustomerByEmail(principal.getName()).getData()).getId();
		String principalPassword = ((Customer)customerService.getCustomerByEmail(principal.getName()).getData()).getPassword();
		LOGGER.info("credential: {}", SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
		LOGGER.info("principalPassword: {}", principalPassword);
		
		if(customer.getId() == principalId){
			ResponseModel rm = customerService.changePassword(customer);		
			entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		}else{
			ResponseModel rm = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_UNAUTHORIZED, "Unauthorized to change password", null);
			entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		}
		
		return entity;
	}
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseModel> resetPassword(@RequestBody Customer customer) {
		LOGGER.info("resetPassword: {}",customer.getId());
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm = customerService.resetPassword(customer);
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/verify", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<ResponseModel> verify(@RequestBody Customer customer) {
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = customerService.verifyCustomer(customer);		
		//LOGGER.info("updated customer{}", rm.getData().toString());
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	/*@RequestMapping(value = "/mobileVerify", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<ResponseModel> mobileVerify(@RequestBody Customer customer) {
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = customerService.mobileVerifyCustomer(customer);		
		//LOGGER.info("updated customer{}", rm.getData().toString());
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}*/
	
	@RequestMapping(value = "/survey", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<ResponseModel> survey(@RequestBody Customer customer) {
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = customerService.surveyCustomer(customer);		
		//LOGGER.info("updated customer{}", rm.getData().toString());
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  getAllCustomers() {
		ResponseEntity<ResponseModel> respMdl = null;
		LOGGER.info("getAllCustomers invoked");
		
		ResponseModel rm = customerService.getAllCustomers();
		
		respMdl = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return respMdl;
	}
	
	
	@RequestMapping(value = "/customerDetail", method = RequestMethod.POST)
	public ResponseEntity<ResponseModel>  getCustomersByEmail(@RequestBody Customer customer) {
		LOGGER.info("customerDetail invoked");
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = customerService.getCustomerByEmail(customer.getEmail());
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
//	public String getCustomer(@PathVariable("id") int id) {
	public ResponseEntity<ResponseModel>  getCustomer(@PathVariable("id") int id) {
		
		ResponseEntity<ResponseModel> entity = null;
		HttpHeaders headers = new HttpHeaders();
		ResponseModel rm = customerService.getCustomer(id);
		
		headers.add("message", rm.getMsg());
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		
		return entity;
	}
	
	/*@RequestMapping(value = "/profile", method = RequestMethod.GET)
//	public String getCustomer(@PathVariable("id") int id) {
	public ResponseEntity<ResponseModel>  getCustomerProfile() {
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = new ResponseModel(HttpStatus.OK, 0, "success", security.getProfile());
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}*/
	
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  getCustomerProfile(Principal principal) {
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = customerService.getCustomerByEmail(principal.getName());
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	/*@RequestMapping(value = "/profile/{id}", method = RequestMethod.GET)
//	public String getCustomer(@PathVariable("id") int id) {
	public ResponseEntity<ResponseModel>  getCustomerProfile(@PathVariable("id") int id) {
		
		ResponseEntity<ResponseModel> entity = null;
		HttpHeaders headers = new HttpHeaders();
		ResponseModel rm = customerService.getCustomerProfile(id);
		
		headers.add("message", rm.getMsg());
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}*/
	
	@RequestMapping(value = "/profile", method = RequestMethod.PUT)
//	public String getCustomer(@PathVariable("id") int id) {
	public ResponseEntity<ResponseModel>  updateCustomerProfile(@RequestBody Customer customer) {
		
		ResponseEntity<ResponseModel> entity = null;
		HttpHeaders headers = new HttpHeaders();
		ResponseModel rm = customerService.updateCustomerProfile(customer);
		
		headers.add("message", rm.getMsg());
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/file/view/{filename:.+}")
	@ResponseBody
	public ResponseEntity<?> viewFile(@PathVariable String filename) {
		LOGGER.info("filename: " + filename);
		String folderPath = StaticFields.PATH_CUSTOMER_PICTURE();
		
		ResponseEntity<Object> entity = null;
		HttpHeaders headers = new HttpHeaders();
		// headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
		if(filename.endsWith(".jpg")){
			headers.add(HttpHeaders.CONTENT_TYPE, "image/jpg");
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename);
		}else if(filename.endsWith(".png")){
			headers.add(HttpHeaders.CONTENT_TYPE, "image/png");
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename);
		}
		try {
			
			entity = new ResponseEntity<Object>(resourceLoader.getResource("file:" + Paths.get(folderPath, filename).toString()), headers, HttpStatus.OK);
			
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.notFound().build();
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/file/download/{filename:.+}")
	@ResponseBody
	public ResponseEntity<?> getFile(@PathVariable String filename) {
		LOGGER.info("filename: " + filename);
		String folderPath = null;
		if(filename.contains("idcard"))
			folderPath = StaticFields.PATH_ID_CARD();
		else if(filename.contains("picture_"))
			folderPath = StaticFields.PATH_CUSTOMER_PICTURE();
		
		ResponseEntity<Object> entity = null;
		HttpHeaders headers = new HttpHeaders();
		// headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
		if(filename.endsWith(".doc")){
			headers.add(HttpHeaders.CONTENT_TYPE, "application/msword");
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
		}else if(filename.endsWith(".docx")){
			headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
		}else if(filename.endsWith(".pdf")){
			headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
		}else if(filename.endsWith(".jpg")){
			headers.add(HttpHeaders.CONTENT_TYPE, "image/jpg");
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
		}else if(filename.endsWith(".png")){
			headers.add(HttpHeaders.CONTENT_TYPE, "image/png");
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
		}
		try {
			
			entity = new ResponseEntity<Object>(resourceLoader.getResource("file:" + Paths.get(folderPath, filename).toString()), headers, HttpStatus.OK);
			
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.notFound().build();
		}
	}
}
