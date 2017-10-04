package com.sumi.transaku.core.controllers;

import javax.mail.MessagingException;

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

import com.sumi.transaku.core.configs.MailConfiguration;
import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.domains.User;
import com.sumi.transaku.core.services.MailService;
import com.sumi.transaku.core.services.UserService;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;

@Controller
@RequestMapping("/user")
public class UserController {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	UserService userService;

//	@Autowired
//	RoleService roleService;

	@Autowired
	MailConfiguration mailConfig;

	@Autowired
	MailService mailService;

	@Autowired
	Utils utils;
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseModel> add(@RequestBody User user) {
		LOGGER.info("register: "+user.getName());
		ResponseEntity<ResponseModel> entity = null;
		String plainPassword = user.getPassword();
		
		user.setPassword(utils.passwordEncoder().encode(user.getPassword()));
		
		ResponseModel rm = userService.addUser(user);
		
		//User userSaved = (User)rm.getData();
		
		//send email notification
		if(rm!=null && ((User)rm.getData())!=null){
			User userSaved = (User)rm.getData();
			//mail to user
			String namaUser = user.getName();
			String subject = StaticFields.EMAIL_CUSTOMER_REGISTER_SUBJECT;
			String recipient = user.getEmail();
			String content = "";
			content += StaticFields.SMS_CUSTOMER_REGISTER_CONTENT.replace("xxxx", user.getEmail());
			content = content.replace("****", plainPassword);
			try {
				mailService.sendMailWithInline(mailConfig.getFrom(), namaUser, subject, recipient, content);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			
			//mail to operator
			mailService.sendEmail(mailConfig.getFrom(), userService.getOperatorEmails(), "New User Registration", "\n\nNama User: " + userSaved.getName());
			
		}
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<ResponseModel> update(@RequestBody User user) {
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm = userService.updateUser(user);		
		LOGGER.info("updated user{}", rm.getData().toString());
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<ResponseModel> delete(@RequestBody User user) {
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rmUser = userService.getUser(user.getId());
		User userToDelete = (User)rmUser.getData();
		userToDelete.setEnabled(StaticFields.STATUS_DISABLED);
		
		ResponseModel rm = userService.deleteUser(userToDelete);		
		
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "/verify", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseModel> verify(@RequestBody User user) {
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = userService.verifyUser(user);		
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  getAllUsers() {
		LOGGER.info("getAllUsers invoked");
		
		ResponseEntity<ResponseModel> entity = null;
		HttpHeaders headers = new HttpHeaders();
		
		ResponseModel rm = userService.getAllUsers();
		
		headers.add("message", rm.getMsg());
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	
	@RequestMapping(value = "/userDetail", method = RequestMethod.POST)
	public ResponseEntity<ResponseModel>  getUsersByEmail(@RequestBody User user) {
		LOGGER.info("userDetail invoked");
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = userService.getUserByEmail(user.getEmail());
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
//	public String getUser(@PathVariable("id") int id) {
	public ResponseEntity<ResponseModel>  getUser(@PathVariable("id") int id) {
		
		ResponseEntity<ResponseModel> entity = null;
		HttpHeaders headers = new HttpHeaders();
		ResponseModel rm = userService.getUser(id);
		
		headers.add("message", rm.getMsg());
		
		User data = (User)rm.getData();
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		
		return entity;
	}
	
	
}
