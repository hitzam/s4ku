package com.sumi.transaku.core.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;

@Controller
@RequestMapping("/signUp")
public class SignUpController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SignUpController.class);
	
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
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseModel> add(@RequestBody Customer customer) {
		LOGGER.info("register: "+customer.getName());
		ResponseEntity<ResponseModel> entity = null;
		String plainPassword = customer.getPassword();
		
		customer.setPassword(utils.passwordEncoder().encode(customer.getPassword()));
		
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
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseModel> resetPassword(@RequestBody Customer customer) {
		LOGGER.info("resetPassword: {}",customer.getId());
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm = customerService.resetPassword(customer);
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/verification", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<ResponseModel> verification(@RequestBody Customer customer) {
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = customerService.mobileVerifyCustomer(customer);
		//LOGGER.info("updated customer{}", rm.getData().toString());
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "/resendCode", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<ResponseModel> resendCode(@RequestBody Customer customer) {
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = customerService.resendCode(customer.getId());		
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	
}
