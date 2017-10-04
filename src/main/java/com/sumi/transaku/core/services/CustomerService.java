package com.sumi.transaku.core.services;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sumi.transaku.core.configs.GeneralConfig;
import com.sumi.transaku.core.domains.Customer;
import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.domains.Supplier;
import com.sumi.transaku.core.repositories.CustomerRepository;
import com.sumi.transaku.core.repositories.RoleRepository;
import com.sumi.transaku.core.utils.AwsS3Util;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;


@Service
public class CustomerService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);
	
	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	SmsService smsService;

	@Autowired
	SupplierService supplierService;
	
	@Autowired
	Utils utils;

	@Autowired
	AwsS3Util awsS3Util;
	
	@Autowired
	GeneralConfig generalConfig;
	
	public ResponseModel addCustomer(Customer customer) {
		LOGGER.info("add customer");
		ResponseModel model = null;
		//String pathSuratPendirian = StaticFields.PATH_SURAT_PENDIRIAN();
		String pathIdCard = StaticFields.PATH_ID_CARD();
		String pathPicture = StaticFields.PATH_CUSTOMER_PICTURE();
		String verificationCode = null;
		try {
			if(customer.getIsSeller()==1){	//SELLER
				verificationCode = utils.generateVerificationCode();

				if(customer.getIdCardBase64()!=null && customer.getIdCardBase64().length()>10){
					//String decodedPicture = URLDecoder.decode(customer.getKartuIdentitasBase64(), "UTF-8");
					String decodedPicture = customer.getIdCardBase64();
					//customer.setIdCardPath(Utils.base64ToFile(decodedPicture, pathIdCard, "idcard_", customer.getIdCardNumber()));
					
					//test image cloud upload
					String keyName = "seller-idcard-"+customer.getIdCardNumber()+".jpg";
					awsS3Util.uploadPicture(StaticFields.AWS_S3_SELLER_BUCKET,Utils.base64ToTmpFile(decodedPicture), keyName);
					customer.setIdCardPath(StaticFields.AWS_S3_SELLER_URL+keyName);
				}
				if(customer.getPictureBase64()!=null && customer.getPictureBase64().length()>10){
					//String decodedPicture = URLDecoder.decode(customer.getPictureBase64(), "UTF-8");
					//LOGGER.info("after:\n"+decodedPicture);
					String decodedPicture = customer.getPictureBase64();
					//customer.setPicturePath(Utils.base64ToFile(decodedPicture, pathPicture, "picture_", customer.getEmail()));
					
					//test image cloud upload
					String keyName = "seller-picture-"+customer.getIdCardNumber()+".jpg";
					awsS3Util.uploadPicture(StaticFields.AWS_S3_SELLER_BUCKET,Utils.base64ToTmpFile(decodedPicture), keyName);
					customer.setPicturePath(StaticFields.AWS_S3_SELLER_URL+keyName);
				}
				
				//Set trial period
				customer.setExpiredDate(utils.generateSellerTrialPeriodDate(1));
				customer.setStatus(StaticFields.STATUS_CUSTOMER_REGISTERED);
				customer.setCreatedDate(new Date());
				customer.setRole(roleRepository.findOne(StaticFields.ROLE_SELLER));

				customer.setVerificationCode(verificationCode);
				customer.setCodeExpired(utils.generateBuyerCodeExpired());

				Customer customerSaved = customerRepository.save(customer);
				System.out.println(customerSaved);        
				
				if(customerSaved != null){
					LOGGER.info(customerSaved.toString());
					
					//send confirmation via sms
					//smsService.sendSms(customer.getPhone(), StaticFields.SMS_CUSTOMER_REGISTER_CONTENT);
					smsService.sendSms(customer.getPhone(), StaticFields.SMS_CUSTOMER_REGISTER_BUYER_CONTENT.replace("xxxxxx", verificationCode));

						
					model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", customerSaved.getId());
				}else{
					model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_FAILED, "failed to add customer", null);
				}
			}else{	//BUYER

				/*if(customer.getIdCardBase64()!=null && customer.getIdCardBase64().length()>10){
					//String decodedPicture = URLDecoder.decode(customer.getKartuIdentitasBase64(), "UTF-8");
					String decodedPicture = customer.getIdCardBase64();
					customer.setIdCardPath(Utils.base64ToFile(decodedPicture, pathIdCard, "idcard_", customer.getIdCardNumber()));
				}*/
				if(customer.getPictureBase64()!=null && customer.getPictureBase64().length()>10){
					//String decodedPicture = URLDecoder.decode(customer.getKartuIdentitasBase64(), "UTF-8");
					String decodedPicture = customer.getPictureBase64();
					customer.setPicturePath(Utils.base64ToFile(decodedPicture, pathPicture, "picture_", customer.getEmail()));
				}
				
				verificationCode = utils.generateVerificationCode();
				customer.setVerificationCode(verificationCode);
				
				//Set trial period
				customer.setCodeExpired(utils.generateBuyerCodeExpired());
				customer.setStatus(StaticFields.STATUS_CUSTOMER_REGISTERED);
				customer.setCreatedDate(new Date());
				customer.setRole(roleRepository.findOne(StaticFields.ROLE_BUYER));
				
				Customer customerSaved = customerRepository.save(customer);
				System.out.println(customerSaved);        
				
				if(customerSaved != null){
					LOGGER.info(customerSaved.toString());
					
					//send confirmation via sms
					smsService.sendSms(customer.getPhone(), StaticFields.SMS_CUSTOMER_REGISTER_BUYER_CONTENT.replace("xxxxxx", verificationCode));
					
					model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", customerSaved.getId());
				}else{
					model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_FAILED, "failed to add customer", null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return model;
	}
	
	public ResponseModel updateCustomer(Customer customer) {
		String pathIdCard = StaticFields.PATH_ID_CARD();
		String pathPicture = StaticFields.PATH_CUSTOMER_PICTURE();
		if(customer.getEnabled()==StaticFields.STATUS_ENABLED)
			LOGGER.info("update customer");
		else
			LOGGER.info("delete customer");
		
		ResponseModel model = null;
		
		Customer existingCust = customerRepository.findOne(customer.getId());
		customer.setStatus(existingCust.getStatus());
		customer.setEnabled(existingCust.getEnabled());
		
		if(customer.getPassword()!=null && !customer.getPassword().equalsIgnoreCase("")){
			customer.setPassword(utils.passwordEncoder().encode(customer.getPassword()));
		}else{
			customer.setPassword(existingCust.getPassword());
		}
		
		if(customer.getIdCardBase64()!=null && customer.getIdCardBase64().length()>10){
			//String decodedPicture = URLDecoder.decode(customer.getKartuIdentitasBase64(), "UTF-8");
			String decodedPicture = customer.getIdCardBase64();
			//customer.setIdCardPath(Utils.base64ToFile(decodedPicture, pathIdCard, "idcard_", customer.getIdCardNumber()));
			
			//test image cloud upload
			String keyName = "seller-idcard-"+customer.getIdCardNumber()+".jpg";
			awsS3Util.uploadPicture(StaticFields.AWS_S3_SELLER_BUCKET,Utils.base64ToTmpFile(decodedPicture), keyName);
			customer.setIdCardPath(StaticFields.AWS_S3_SELLER_URL+keyName);
		}else{
			customer.setIdCardPath(existingCust.getIdCardPath());
		}
		
		if(customer.getPictureBase64()!=null && customer.getPictureBase64().length()>10){
			//String decodedPicture = URLDecoder.decode(customer.getKartuIdentitasBase64(), "UTF-8");
			String decodedPicture = customer.getPictureBase64();
			//customer.setPicturePath(Utils.base64ToFile(decodedPicture, pathPicture, "picture_", customer.getEmail()));
			
			//test image cloud upload
			String keyName = "seller-picture-"+customer.getIdCardNumber()+".jpg";
			awsS3Util.uploadPicture(StaticFields.AWS_S3_SELLER_BUCKET,Utils.base64ToTmpFile(decodedPicture), keyName);
			customer.setPicturePath(StaticFields.AWS_S3_SELLER_URL+keyName);
		}else{
			customer.setPicturePath(existingCust.getPicturePath());
		}
		
		Customer customerUpdated = customerRepository.save(customer);
		System.out.println(customerUpdated);        
		
		if(customerUpdated != null){
			LOGGER.info(customerUpdated.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", customerUpdated);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to update customer", null);
		}
		
		return model;
	}
	
	@Deprecated
	public ResponseModel updateCustomerProfileOld(Customer customer) {
		ResponseModel model = null;
		
		Customer existingCust = customerRepository.findOne(customer.getId());
		
		if(existingCust!=null){
			existingCust.setName(customer.getName());
			existingCust.setEmail(customer.getEmail());
			existingCust.setPhone(customer.getPhone());
			existingCust.setGender(customer.getGender());
		}
		
		Customer customerUpdated = customerRepository.save(customer);
		System.out.println(customerUpdated);        
		
		if(customerUpdated != null){
			LOGGER.info(customerUpdated.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", customerUpdated);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to update customer", null);
		}
		
		return model;
	}
	
	public ResponseModel updateCustomerProfile(Customer customer) {
		String pathIdCard = StaticFields.PATH_ID_CARD();
		String pathPicture = StaticFields.PATH_CUSTOMER_PICTURE();
		
		ResponseModel model = null;
		
		Customer existingCust = customerRepository.findOne(customer.getId());
		
		customer.setPassword(existingCust.getPassword());
		customer.setStatus(existingCust.getStatus());
		customer.setEnabled(existingCust.getEnabled());
		customer.setCreatedDate(existingCust.getCreatedDate());
		
		if(customer.getIdCardBase64()!=null && customer.getIdCardBase64().length()>10){
			//String decodedPicture = URLDecoder.decode(customer.getKartuIdentitasBase64(), "UTF-8");
			String decodedPicture = customer.getIdCardBase64();
			//customer.setIdCardPath(Utils.base64ToFile(decodedPicture, pathIdCard, "idcard_", customer.getIdCardNumber()));
			
			String keyName = "seller-idcard-"+customer.getIdCardNumber()+".jpg";
			awsS3Util.uploadPicture(StaticFields.AWS_S3_SELLER_BUCKET,Utils.base64ToTmpFile(decodedPicture), keyName);
			customer.setIdCardPath(StaticFields.AWS_S3_SELLER_URL+keyName);
		}else{
			customer.setIdCardPath(existingCust.getIdCardPath());
		}
		
		if(customer.getPictureBase64()!=null && customer.getPictureBase64().length()>10){
			//String decodedPicture = URLDecoder.decode(customer.getKartuIdentitasBase64(), "UTF-8");
			String decodedPicture = customer.getPictureBase64();
			//customer.setPicturePath(Utils.base64ToFile(decodedPicture, pathPicture, "picture_", customer.getEmail()));
			
			//test image cloud upload
			String keyName = "seller-picture-"+customer.getIdCardNumber()+".jpg";
			awsS3Util.uploadPicture(StaticFields.AWS_S3_SELLER_BUCKET,Utils.base64ToTmpFile(decodedPicture), keyName);
			customer.setPicturePath(StaticFields.AWS_S3_SELLER_URL+keyName);
			
			//update supplier picture if convert seller to supplier active
			if(generalConfig.getConvertSellerSupplier()==1){
				supplierService.updateSupplierPicturePath(customer.getEmail(), StaticFields.AWS_S3_SELLER_URL+keyName);
			}
		}else{
			customer.setPicturePath(existingCust.getPicturePath());
		}
		
		Customer customerUpdated = customerRepository.save(customer);
		System.out.println(customerUpdated);        
		
		if(customerUpdated != null){
			LOGGER.info(customerUpdated.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", customerUpdated);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to update customer", null);
		}
		
		return model;
	}
	
	public ResponseModel changePassword(Customer customer) {
		
		ResponseModel model = null;
		
		//Customer existingCust = customerRepository.findOne(customer.getId());
		Customer existingCust = customerRepository.findByIdAndStatus(customer.getId(), StaticFields.STATUS_CUSTOMER_VERIFIED);
		
		LOGGER.info("customer id: {}", customer.getId());
		LOGGER.info("existing password: {}", existingCust.getPassword());
		LOGGER.info("old password: {}", customer.getOldPassword());
		LOGGER.info("new password: {}", customer.getPassword());
		
		if(customer.getPassword()!=null && !customer.getPassword().equalsIgnoreCase("")){
			if(utils.passwordEncoder().matches(customer.getOldPassword(), existingCust.getPassword())){
				existingCust.setPassword(utils.passwordEncoder().encode(customer.getPassword()));
				customerRepository.save(existingCust);
				model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", null);
			}else{
				model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_SUCCESS, "old password do not match", null);
			}
		}
				
		return model;
	}

	public ResponseModel resetPassword(Customer customer) {

		ResponseModel model = null;


		if (customer.getEmail() != null && !customer.getEmail().equalsIgnoreCase("")) {
			Customer existingCustomer = customerRepository.findByEmail(customer.getEmail());
			if (existingCustomer != null) {
				String newPassword = utils.generatePassword();
				existingCustomer.setPassword(utils.passwordEncoder().encode(newPassword));
				customerRepository.save(existingCustomer);

				//send confirmation via sms
				try {
					smsService.sendSms(existingCustomer.getPhone(), StaticFields.SMS_CUSTOMER_RESET_PASSWORD.replace("{password}", newPassword));
				} catch (Exception e) {
					LOGGER.error(e.getMessage());
					e.printStackTrace();
				}

				model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "Password baru telah dikirim ke nomor "+existingCustomer.getPhone(), null);
			} else {
				model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_SUCCESS,
						"customer's email do not match", null);
			}
		}

		return model;
	}
	
	public ResponseModel verifyCustomer(Customer customer) {
		LOGGER.info("verify customer");
		
		ResponseModel model = null;
		customer = customerRepository.findOne(customer.getId());
		customer.setStatus(StaticFields.STATUS_CUSTOMER_VERIFIED);
		customer.setEnabled(StaticFields.STATUS_ENABLED);
		
		if(customer.getIsSeller() == 1)
			customer.setExpiredDate(utils.generateSellerTrialPeriodDate(0));
		Customer customerVerified = customerRepository.save(customer);
		
		System.out.println(customerVerified);        
		
		if(customerVerified != null){
			//LOGGER.info(customerVerified.toString());
			//send confirmation via sms
			try {
				smsService.sendSms(customerVerified.getPhone(), StaticFields.SMS_CUSTOMER_VERIFIED_CONTENT);
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
				e.printStackTrace();
			}
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", null);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to verify customer", null);
		}
		
		return model;
	}
	
	public ResponseModel mobileVerifyCustomer(Customer customer) {
		LOGGER.info("mobileVerify customer");
		
		ResponseModel model = null;
		Customer existingCustomer = customerRepository.findOne(customer.getId());
		Customer customerVerified = null;
		String password = null;
		if(customer.getVerificationCode().contentEquals(existingCustomer.getVerificationCode())){
			password = utils.generatePassword();
			existingCustomer.setStatus(StaticFields.STATUS_CUSTOMER_VERIFIED);
			existingCustomer.setEnabled(StaticFields.STATUS_ENABLED);
			//existingCustomer.setPassword(utils.passwordEncoder().encode(password));
			customerVerified = customerRepository.save(existingCustomer);
		}
		
		System.out.println(customerVerified);        
		
		if(customerVerified != null){
			try {
				//smsService.sendSms(customerVerified.getPhone(), StaticFields.EMAIL_CUSTOMER_VERIFIED_CONTENT.replace("xxxx", customerVerified.getEmail()).replace("****", password));
				smsService.sendSms(customerVerified.getPhone(), StaticFields.EMAIL_CUSTOMER_VERIFIED_CONTENT);
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
				e.printStackTrace();
			}
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", null);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_UNAUTHORIZED, "failed to verify customer", null);
		}
		
		return model;
	}
	
	public ResponseModel surveyCustomer(Customer customer) {
		LOGGER.info("survey customer");
		
		ResponseModel model = null;
		customer = customerRepository.findOne(customer.getId());
		customer.setStatus(StaticFields.STATUS_CUSTOMER_SURVEYED);
		customer.setExpiredDate(null);
		Customer customerSurveyed = customerRepository.save(customer);
		System.out.println(customerSurveyed);        
		
		if(customerSurveyed != null){
			//LOGGER.info(customerVerified.toString());
			//send confirmation via sms
			try {
				smsService.sendSms(customerSurveyed.getPhone(), StaticFields.EMAIL_CUSTOMER_SURVEYED_CONTENT);
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
				e.printStackTrace();
			}
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", null);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to verify customer", null);
		}
		
		return model;
	}
	
	public ResponseModel deleteCustomer(Customer customer) {
		LOGGER.info("delete customer");
		
		ResponseModel model = null;
		customerRepository.delete(customer);
		
		model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", null);
		
		return model;
	}
	
	
	public ResponseModel getAllCustomers(){
		LOGGER.info("get All Customer");
		ResponseModel model = null;
		List<Customer> customers = customerRepository.findAll();
		
		if(customers != null){
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", customers);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getCustomer(int customerId){
		LOGGER.info("get customer");
		ResponseModel model = null;
		Customer customer = customerRepository.findOne(customerId);
		System.out.println(customer);        
		
		if(customer != null){
			LOGGER.info(customer.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", customer);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getCustomerProfile(int customerId){
		LOGGER.info("get customer");
		ResponseModel model = null;
		Customer customer = customerRepository.getCustomerProfile(customerId);
		System.out.println(customer);        
		
		if(customer != null){
			LOGGER.info(customer.toString());
			customer.setPassword(null);
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", customer);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getCustomerByEmail(String email){
		LOGGER.info("get customer "+email);
		ResponseModel model = null;
//		Customer customer = customerRepository.findByEmailContaining(email);
		Customer customer = customerRepository.findByEmail(email);
		System.out.println(customer);        
		
		if(customer != null){
			LOGGER.info(customer.toString());
			//clear password
			//customer.setPassword(null);
			customer.setProvince(customer.getCity().getProvince());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", customer);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	
	public ResponseModel resendCode(int id){
		LOGGER.info("resendCode "+id);
		ResponseModel model = null;
        Customer customer = customerRepository.findByIdAndStatus(id, StaticFields.STATUS_CUSTOMER_REGISTERED);
        
        try {
        if(customer != null && customer.getVerificationCode() != null){
        	customer.setPassword(null);

				smsService.sendSms(customer.getPhone(), StaticFields.SMS_CUSTOMER_REGISTER_BUYER_CONTENT.replace("xxxxxx", customer.getVerificationCode()));
			
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "Verification code has been sent to "+customer.getPhone(), true);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
        } catch (Exception e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        
        return model;
	}

	
	
	public String getOperatorEmails(int status){
		LOGGER.info("getOperatorEmails");
		StringBuilder sb = new StringBuilder();
		
		List<Customer> customers = customerRepository.findByStatus(status);
		for (Customer customer : customers) {
			sb.append(customer.getEmail());
			sb.append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	public long countCustomerRegistered(int isSeller){
		LOGGER.info("countCustomerRegistered");
		
		long count = customerRepository.countByIsSeller(isSeller);
		System.out.println(count);        
		
		return count;
	}
	
	public long countCustomerVerified(){
		LOGGER.info("countCustomerVerified");
        long count = customerRepository.countByEnabled(StaticFields.STATUS_ENABLED);
        System.out.println(count);        
        
        return count;
	}

}
