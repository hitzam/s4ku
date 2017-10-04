package com.sumi.transaku.core.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sumi.transaku.core.domains.QtyUnit;
import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.repositories.QtyUnitRepository;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;


@Service
public class QtyUnitService {
	private static final Logger LOGGER = LoggerFactory.getLogger(QtyUnitService.class);
	
	@Autowired
	QtyUnitRepository qtyUnitRepository;

	@Autowired
	Utils utils;
	
	
	public ResponseModel getAllUnits(){
		LOGGER.info("get All Unit");
		ResponseModel model = null;
		List<QtyUnit> units = qtyUnitRepository.findAll();
		
		if(units != null){
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", units);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getUnit(int unitId){
		LOGGER.info("get unit");
		ResponseModel model = null;
		QtyUnit unit = qtyUnitRepository.findOne(unitId);
		System.out.println(unit);        
		
		if(unit != null){
			LOGGER.info(unit.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", unit);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getUnitByName(String name){
		LOGGER.info("get UnitByName "+name);
		ResponseModel model = null;
//		City city = cityRepository.findByEmailContaining(email);
		QtyUnit unit = qtyUnitRepository.findByName(name);
		System.out.println(unit);        
		
		if(unit != null){
			LOGGER.info(unit.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", unit);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
}
