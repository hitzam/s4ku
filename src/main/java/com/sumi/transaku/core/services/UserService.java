package com.sumi.transaku.core.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.domains.Role;
import com.sumi.transaku.core.domains.User;
import com.sumi.transaku.core.repositories.RoleRepository;
import com.sumi.transaku.core.repositories.UserRepository;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;


@Service
public class UserService {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	Utils utils;
	
	public ResponseModel getAllUsers(){
		LOGGER.info("get All User");
		ResponseModel model = null;
		List<User> users = userRepository.findAll();
		
		if(users != null){
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", users);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getUser(int userId){
		LOGGER.info("get user");
		ResponseModel model = null;
		User user = userRepository.findOne(userId);
		System.out.println(user);        
		
		if(user != null){
			LOGGER.info(user.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", user);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getUserByEmail(String email){
		LOGGER.info("get user "+email);
		ResponseModel model = null;
//		User user = userRepository.findByEmailContaining(email);
        User user = userRepository.findByemail(email);
        System.out.println(user);        
        
        if(user != null){
        	LOGGER.info(user.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", user);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
        
        return model;
	}

	//@Transactional
	public ResponseModel addUser(User user) {
		LOGGER.info("add user");
		ResponseModel model = null;
			
		User userSaved = userRepository.save(user);
		System.out.println(userSaved);        
		
		if(userSaved != null){
			LOGGER.info(userSaved.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", userSaved);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_FAILED, "failed to add user", null);
		}
		
		return model;
	}
	
	public ResponseModel updateUser(User user) {
		
		ResponseModel model = null;
		
		User existingUsr = userRepository.findOne(user.getId());
		
		if(user.getPassword()!=null && !user.getPassword().equalsIgnoreCase("")){
			user.setPassword(utils.passwordEncoder().encode(user.getPassword()));
		}else{
			user.setPassword(existingUsr.getPassword());
		}
		
		User userUpdated = userRepository.save(user);
		System.out.println(userUpdated);        
		
		if(userUpdated != null){
			LOGGER.info(userUpdated.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", userUpdated);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_FAILED, "failed to update user", null);
		}
		
		return model;
	}
	
	public ResponseModel verifyUser(User user) {
		LOGGER.info("verify user");
		
		ResponseModel model = null;
		user = userRepository.findOne(user.getId());
		user.setEnabled(StaticFields.STATUS_ENABLED);
		User userVerified = userRepository.save(user);
		System.out.println(userVerified);        
		
		if(userVerified != null){
			//LOGGER.info(userVerified.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", userVerified);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_FAILED, "failed to verify user", null);
		}
		
		return model;
	}
	
	public ResponseModel deleteUser(User user) {
		LOGGER.info("delete user");
		
		ResponseModel model = null;
		userRepository.delete(user);
		
		model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", 1);
		
		return model;
	}
	
	
	public String getOperatorEmails(){
		LOGGER.info("getOperatorEmails");
		StringBuilder sb = new StringBuilder();
		
		Role role = new Role(StaticFields.ROLE_OPERATOR);
		
		List<User> users = userRepository.findByRole(role);
		for (User user : users) {
			sb.append(user.getEmail());
			sb.append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
/*	public long countUserRegistered(){
		LOGGER.info("countUserRegistered");
		Role role = new Role(StaticFields.ROLE_USER);
		
		long count = userRepository.countByRole(role);
		System.out.println(count);        
		
		return count;
	}
	
	public long countUserVerified(){
		LOGGER.info("countUserVerified");
		Role role = new Role(StaticFields.ROLE_USER);
        long count = userRepository.countByRoleAndEnabled(role, StaticFields.STATUS_ENABLED);
        System.out.println(count);        
        
        return count;
	}*/
}
