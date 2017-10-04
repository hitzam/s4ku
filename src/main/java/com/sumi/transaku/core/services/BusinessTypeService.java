package com.sumi.transaku.core.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sumi.transaku.core.domains.BusinessType;
import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.repositories.BusinessTypeRepository;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;


@Service
public class BusinessTypeService {
	private static final Logger LOGGER = LoggerFactory.getLogger(BusinessTypeService.class);
	
	@Autowired
	BusinessTypeRepository businessTypeRepository;

	@Autowired
	Utils utils;
	
	
	
	public ResponseModel getAllBusinessTypes(){
		LOGGER.info("get All BusinessType");
		ResponseModel model = null;
		List<BusinessType> businessTypes = businessTypeRepository.findAll();
		
		if(businessTypes != null){
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", businessTypes);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getBusinessType(int businessTypeId){
		LOGGER.info("get businessType");
		ResponseModel model = null;
		BusinessType businessType = businessTypeRepository.findOne(businessTypeId);
		System.out.println(businessType);        
		
		if(businessType != null){
			LOGGER.info(businessType.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", businessType);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getBusinessTypeByName(String name){
		LOGGER.info("get businessTypeByName "+name);
		ResponseModel model = null;
//		BusinessType businessType = businessTypeRepository.findByEmailContaining(email);
        BusinessType businessType = businessTypeRepository.findByName(name);
        System.out.println(businessType);        
        
        if(businessType != null){
        	LOGGER.info(businessType.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", businessType);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
        
        return model;
	}

	
	
	/*public ResponseModel addBusinessType(BusinessType businessType) {
	LOGGER.info("add businessType");
	ResponseModel model = null;
	//String pathSuratPendirian = StaticFields.PATH_SURAT_PENDIRIAN();
	String pathIdCard = StaticFields.PATH_ID_CARD();
	String pathPicture = StaticFields.PATH_ID_CARD();
	String verificationCode = null;
	try {
		if(businessType.getIsSeller()==1){	//SELLER
			
			if(businessType.getIdCardBase64()!=null && businessType.getIdCardBase64().length()>10){
				//String decodedPicture = URLDecoder.decode(businessType.getKartuIdentitasBase64(), "UTF-8");
				String decodedPicture = businessType.getIdCardBase64();
				businessType.setIdCardPath(Utils.base64ToFile(decodedPicture, pathIdCard, "idcard_", businessType.getIdCardNumber()));
			}
			if(businessType.getPictureBase64()!=null && businessType.getPictureBase64().length()>10){
				//String decodedPicture = URLDecoder.decode(businessType.getKartuIdentitasBase64(), "UTF-8");
				String decodedPicture = businessType.getIdCardBase64();
				businessType.setPicturePath(Utils.base64ToFile(decodedPicture, pathPicture, "picture_", businessType.getEmail()));
			}
			
			if(businessType.getIsSeller()==0){
				verificationCode = Utils.generateVerificationCode();
				businessType.setVerificationCode(verificationCode);
			}
			
			//Set trial period
			businessType.setExpiredDate(utils.generateTrialPeriodDate());
			businessType.setStatus(StaticFields.STATUS_CUSTOMER_REGISTERED);
			businessType.setCreatedDate(new Date());
			
			BusinessType businessTypeSaved = businessTypeRepository.save(businessType);
			System.out.println(businessTypeSaved);        
			
			if(businessTypeSaved != null){
				LOGGER.info(businessTypeSaved.toString());
				
				//send confirmation via sms
				if(businessType.getIsSeller()==1){
					smsService.sendSms(businessType.getPhone(), StaticFields.EMAIL_CUSTOMER_REGISTER_CONTENT);
				}else{
					smsService.sendSms(businessType.getPhone(), StaticFields.EMAIL_CUSTOMER_REGISTER_BUYER_CONTENT.replace("xxxxxx", verificationCode));
				}
				model = new ResponseModel(HttpStatus.OK, StaticFields.RESPONSE_CODE_SUCCESS, "success", true);
			}else{
				model = new ResponseModel(HttpStatus.OK, StaticFields.RESPONSE_CODE_FAILED, "failed to add businessType", false);
			}
		}else{	//BUYER
			
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	return model;
}

public ResponseModel updateBusinessType(BusinessType businessType) {
	String pathIdCard = StaticFields.PATH_ID_CARD();
	String pathPicture = StaticFields.PATH_ID_CARD();
	if(businessType.getEnabled()==StaticFields.STATUS_ENABLED)
		LOGGER.info("update businessType");
	else
		LOGGER.info("delete businessType");
	
	ResponseModel model = null;
	
	BusinessType existingCust = businessTypeRepository.findOne(businessType.getId());
	
	if(businessType.getPassword()!=null && !businessType.getPassword().equalsIgnoreCase("")){
		businessType.setPassword(utils.passwordEncoder().encode(businessType.getPassword()));
	}else{
		businessType.setPassword(existingCust.getPassword());
	}
	
	if(businessType.getIdCardBase64()!=null && businessType.getIdCardBase64().length()>10){
		//String decodedPicture = URLDecoder.decode(businessType.getKartuIdentitasBase64(), "UTF-8");
		String decodedPicture = businessType.getIdCardBase64();
		businessType.setIdCardPath(Utils.base64ToFile(decodedPicture, pathIdCard, "idcard_", businessType.getIdCardNumber()));
	}else{
		businessType.setIdCardPath(existingCust.getIdCardPath());
	}
	
	if(businessType.getPictureBase64()!=null && businessType.getPictureBase64().length()>10){
		//String decodedPicture = URLDecoder.decode(businessType.getKartuIdentitasBase64(), "UTF-8");
		String decodedPicture = businessType.getPictureBase64();
		businessType.setPicturePath(Utils.base64ToFile(decodedPicture, pathPicture, "picture_", businessType.getEmail()));
	}else{
		businessType.setPicturePath(existingCust.getPicturePath());
	}
	
	BusinessType businessTypeUpdated = businessTypeRepository.save(businessType);
	System.out.println(businessTypeUpdated);        
	
	if(businessTypeUpdated != null){
		LOGGER.info(businessTypeUpdated.toString());
		model = new ResponseModel(HttpStatus.OK, StaticFields.RESPONSE_CODE_SUCCESS, "success", businessTypeUpdated);
	}else{
		model = new ResponseModel(HttpStatus.OK, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to update businessType", null);
	}
	
	return model;
}

public ResponseModel deleteBusinessType(BusinessType businessType) {
	LOGGER.info("delete businessType");
	
	ResponseModel model = null;
	businessTypeRepository.delete(businessType);
	
	model = new ResponseModel(HttpStatus.OK, StaticFields.RESPONSE_CODE_SUCCESS, "success", 1);
	
	return model;
}
*/
}
