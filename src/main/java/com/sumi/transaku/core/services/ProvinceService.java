package com.sumi.transaku.core.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sumi.transaku.core.domains.Province;
import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.repositories.ProvinceRepository;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;


@Service
public class ProvinceService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProvinceService.class);
	
	@Autowired
	ProvinceRepository provinceRepository;

	@Autowired
	Utils utils;
	
	
	
	public ResponseModel getAllProvinces(){
		LOGGER.info("get All Province");
		ResponseModel model = null;
		List<Province> provinces = provinceRepository.findAll();
		
		if(provinces != null){
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", provinces);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getProvince(int provinceId){
		LOGGER.info("get province");
		ResponseModel model = null;
		Province province = provinceRepository.findOne(provinceId);
		System.out.println(province);        
		
		if(province != null){
			LOGGER.info(province.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", province);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getProvinceByName(String name){
		LOGGER.info("get provinceByName "+name);
		ResponseModel model = null;
//		Province province = provinceRepository.findByEmailContaining(email);
        Province province = provinceRepository.findByName(name);
        System.out.println(province);        
        
        if(province != null){
        	LOGGER.info(province.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", province);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
        
        return model;
	}

	
	
	/*public ResponseModel addProvince(Province province) {
	LOGGER.info("add province");
	ResponseModel model = null;
	//String pathSuratPendirian = StaticFields.PATH_SURAT_PENDIRIAN();
	String pathIdCard = StaticFields.PATH_ID_CARD();
	String pathPicture = StaticFields.PATH_ID_CARD();
	String verificationCode = null;
	try {
		if(province.getIsSeller()==1){	//SELLER
			
			if(province.getIdCardBase64()!=null && province.getIdCardBase64().length()>10){
				//String decodedPicture = URLDecoder.decode(province.getKartuIdentitasBase64(), "UTF-8");
				String decodedPicture = province.getIdCardBase64();
				province.setIdCardPath(Utils.base64ToFile(decodedPicture, pathIdCard, "idcard_", province.getIdCardNumber()));
			}
			if(province.getPictureBase64()!=null && province.getPictureBase64().length()>10){
				//String decodedPicture = URLDecoder.decode(province.getKartuIdentitasBase64(), "UTF-8");
				String decodedPicture = province.getIdCardBase64();
				province.setPicturePath(Utils.base64ToFile(decodedPicture, pathPicture, "picture_", province.getEmail()));
			}
			
			if(province.getIsSeller()==0){
				verificationCode = Utils.generateVerificationCode();
				province.setVerificationCode(verificationCode);
			}
			
			//Set trial period
			province.setExpiredDate(utils.generateTrialPeriodDate());
			province.setStatus(StaticFields.STATUS_CUSTOMER_REGISTERED);
			province.setCreatedDate(new Date());
			
			Province provinceSaved = provinceRepository.save(province);
			System.out.println(provinceSaved);        
			
			if(provinceSaved != null){
				LOGGER.info(provinceSaved.toString());
				
				//send confirmation via sms
				if(province.getIsSeller()==1){
					smsService.sendSms(province.getPhone(), StaticFields.EMAIL_CUSTOMER_REGISTER_CONTENT);
				}else{
					smsService.sendSms(province.getPhone(), StaticFields.EMAIL_CUSTOMER_REGISTER_BUYER_CONTENT.replace("xxxxxx", verificationCode));
				}
				model = new ResponseModel(HttpStatus.OK, StaticFields.RESPONSE_CODE_SUCCESS, "success", true);
			}else{
				model = new ResponseModel(HttpStatus.OK, StaticFields.RESPONSE_CODE_FAILED, "failed to add province", false);
			}
		}else{	//BUYER
			
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	return model;
}

public ResponseModel updateProvince(Province province) {
	String pathIdCard = StaticFields.PATH_ID_CARD();
	String pathPicture = StaticFields.PATH_ID_CARD();
	if(province.getEnabled()==StaticFields.STATUS_ENABLED)
		LOGGER.info("update province");
	else
		LOGGER.info("delete province");
	
	ResponseModel model = null;
	
	Province existingCust = provinceRepository.findOne(province.getId());
	
	if(province.getPassword()!=null && !province.getPassword().equalsIgnoreCase("")){
		province.setPassword(utils.passwordEncoder().encode(province.getPassword()));
	}else{
		province.setPassword(existingCust.getPassword());
	}
	
	if(province.getIdCardBase64()!=null && province.getIdCardBase64().length()>10){
		//String decodedPicture = URLDecoder.decode(province.getKartuIdentitasBase64(), "UTF-8");
		String decodedPicture = province.getIdCardBase64();
		province.setIdCardPath(Utils.base64ToFile(decodedPicture, pathIdCard, "idcard_", province.getIdCardNumber()));
	}else{
		province.setIdCardPath(existingCust.getIdCardPath());
	}
	
	if(province.getPictureBase64()!=null && province.getPictureBase64().length()>10){
		//String decodedPicture = URLDecoder.decode(province.getKartuIdentitasBase64(), "UTF-8");
		String decodedPicture = province.getPictureBase64();
		province.setPicturePath(Utils.base64ToFile(decodedPicture, pathPicture, "picture_", province.getEmail()));
	}else{
		province.setPicturePath(existingCust.getPicturePath());
	}
	
	Province provinceUpdated = provinceRepository.save(province);
	System.out.println(provinceUpdated);        
	
	if(provinceUpdated != null){
		LOGGER.info(provinceUpdated.toString());
		model = new ResponseModel(HttpStatus.OK, StaticFields.RESPONSE_CODE_SUCCESS, "success", provinceUpdated);
	}else{
		model = new ResponseModel(HttpStatus.OK, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to update province", null);
	}
	
	return model;
}

public ResponseModel deleteProvince(Province province) {
	LOGGER.info("delete province");
	
	ResponseModel model = null;
	provinceRepository.delete(province);
	
	model = new ResponseModel(HttpStatus.OK, StaticFields.RESPONSE_CODE_SUCCESS, "success", 1);
	
	return model;
}
*/
}
