package com.sumi.transaku.core.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sumi.transaku.core.domains.City;
import com.sumi.transaku.core.domains.Province;
import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.repositories.CityRepository;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;


@Service
public class CityService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CityService.class);
	
	@Autowired
	CityRepository cityRepository;

	@Autowired
	Utils utils;
	
	
	
	public ResponseModel getAllCities(){
		LOGGER.info("get All City");
		ResponseModel model = null;
		List<City> citys = cityRepository.findAll();
		
		if(citys != null){
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", citys);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getCity(int cityId){
		LOGGER.info("get city");
		ResponseModel model = null;
		City city = cityRepository.findOne(cityId);
		//System.out.println(city);        
		
		if(city != null){
			LOGGER.info(city.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", city);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getCityByName(String name){
		LOGGER.info("get cityByName "+name);
		ResponseModel model = null;
//		City city = cityRepository.findByEmailContaining(email);
		City city = cityRepository.findByName(name);
		//System.out.println(city);        
		
		if(city != null){
			//LOGGER.info(city.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", city);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getCityByProvince(Province province){
		LOGGER.info("get cityByProvince "+province.getName());
		ResponseModel model = null;
//		City city = cityRepository.findByEmailContaining(email);
        List<City> cities = cityRepository.findByProvince(province);
        //System.out.println(cities);        
        
        if(cities != null){
        	//LOGGER.info(cities.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", cities);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
        
        return model;
	}

	
	
	/*public ResponseModel addCity(City city) {
	LOGGER.info("add city");
	ResponseModel model = null;
	//String pathSuratPendirian = StaticFields.PATH_SURAT_PENDIRIAN();
	String pathIdCard = StaticFields.PATH_ID_CARD();
	String pathPicture = StaticFields.PATH_ID_CARD();
	String verificationCode = null;
	try {
		if(city.getIsSeller()==1){	//SELLER
			
			if(city.getIdCardBase64()!=null && city.getIdCardBase64().length()>10){
				//String decodedPicture = URLDecoder.decode(city.getKartuIdentitasBase64(), "UTF-8");
				String decodedPicture = city.getIdCardBase64();
				city.setIdCardPath(Utils.base64ToFile(decodedPicture, pathIdCard, "idcard_", city.getIdCardNumber()));
			}
			if(city.getPictureBase64()!=null && city.getPictureBase64().length()>10){
				//String decodedPicture = URLDecoder.decode(city.getKartuIdentitasBase64(), "UTF-8");
				String decodedPicture = city.getIdCardBase64();
				city.setPicturePath(Utils.base64ToFile(decodedPicture, pathPicture, "picture_", city.getEmail()));
			}
			
			if(city.getIsSeller()==0){
				verificationCode = Utils.generateVerificationCode();
				city.setVerificationCode(verificationCode);
			}
			
			//Set trial period
			city.setExpiredDate(utils.generateTrialPeriodDate());
			city.setStatus(StaticFields.STATUS_CUSTOMER_REGISTERED);
			city.setCreatedDate(new Date());
			
			City citySaved = cityRepository.save(city);
			System.out.println(citySaved);        
			
			if(citySaved != null){
				LOGGER.info(citySaved.toString());
				
				//send confirmation via sms
				if(city.getIsSeller()==1){
					smsService.sendSms(city.getPhone(), StaticFields.EMAIL_CUSTOMER_REGISTER_CONTENT);
				}else{
					smsService.sendSms(city.getPhone(), StaticFields.EMAIL_CUSTOMER_REGISTER_BUYER_CONTENT.replace("xxxxxx", verificationCode));
				}
				model = new ResponseModel(HttpStatus.OK, StaticFields.RESPONSE_CODE_SUCCESS, "success", true);
			}else{
				model = new ResponseModel(HttpStatus.OK, StaticFields.RESPONSE_CODE_FAILED, "failed to add city", false);
			}
		}else{	//BUYER
			
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	return model;
}

public ResponseModel updateCity(City city) {
	String pathIdCard = StaticFields.PATH_ID_CARD();
	String pathPicture = StaticFields.PATH_ID_CARD();
	if(city.getEnabled()==StaticFields.STATUS_ENABLED)
		LOGGER.info("update city");
	else
		LOGGER.info("delete city");
	
	ResponseModel model = null;
	
	City existingCust = cityRepository.findOne(city.getId());
	
	if(city.getPassword()!=null && !city.getPassword().equalsIgnoreCase("")){
		city.setPassword(utils.passwordEncoder().encode(city.getPassword()));
	}else{
		city.setPassword(existingCust.getPassword());
	}
	
	if(city.getIdCardBase64()!=null && city.getIdCardBase64().length()>10){
		//String decodedPicture = URLDecoder.decode(city.getKartuIdentitasBase64(), "UTF-8");
		String decodedPicture = city.getIdCardBase64();
		city.setIdCardPath(Utils.base64ToFile(decodedPicture, pathIdCard, "idcard_", city.getIdCardNumber()));
	}else{
		city.setIdCardPath(existingCust.getIdCardPath());
	}
	
	if(city.getPictureBase64()!=null && city.getPictureBase64().length()>10){
		//String decodedPicture = URLDecoder.decode(city.getKartuIdentitasBase64(), "UTF-8");
		String decodedPicture = city.getPictureBase64();
		city.setPicturePath(Utils.base64ToFile(decodedPicture, pathPicture, "picture_", city.getEmail()));
	}else{
		city.setPicturePath(existingCust.getPicturePath());
	}
	
	City cityUpdated = cityRepository.save(city);
	System.out.println(cityUpdated);        
	
	if(cityUpdated != null){
		LOGGER.info(cityUpdated.toString());
		model = new ResponseModel(HttpStatus.OK, StaticFields.RESPONSE_CODE_SUCCESS, "success", cityUpdated);
	}else{
		model = new ResponseModel(HttpStatus.OK, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to update city", null);
	}
	
	return model;
}

public ResponseModel deleteCity(City city) {
	LOGGER.info("delete city");
	
	ResponseModel model = null;
	cityRepository.delete(city);
	
	model = new ResponseModel(HttpStatus.OK, StaticFields.RESPONSE_CODE_SUCCESS, "success", 1);
	
	return model;
}
*/
}
