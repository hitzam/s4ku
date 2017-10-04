package com.sumi.transaku.core.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sumi.transaku.core.domains.InventoryCategory;
import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.repositories.CategoryRepository;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;


@Service
public class CategoryService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CategoryService.class);
	
	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	Utils utils;
	
	
	
	public ResponseModel getAllCategories(){
		LOGGER.info("get All Categories");
		ResponseModel model = null;
		List<InventoryCategory> categories = categoryRepository.findAll();
		
		if(categories != null){
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", categories);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getCategory(int categoryId){
		LOGGER.info("get city");
		ResponseModel model = null;
		InventoryCategory category = categoryRepository.findOne(categoryId);
		System.out.println(category);        
		
		if(category != null){
			LOGGER.info(category.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", category);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getCategoryByName(String name){
		LOGGER.info("get CategoryByName "+name);
		ResponseModel model = null;
//		City city = cityRepository.findByEmailContaining(email);
		InventoryCategory category = categoryRepository.findByName(name);
		System.out.println(category);        
		
		if(category != null){
			LOGGER.info(category.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", category);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
}
