package com.sumi.transaku.core.controllers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
/*import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;*/
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sumi.transaku.core.domains.City;
import com.sumi.transaku.core.domains.Province;
import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.services.CategoryService;
import com.sumi.transaku.core.services.CityService;
import com.sumi.transaku.core.utils.Utils;

@Controller
@RequestMapping("/category")
public class CategoryController {
	private static final Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);
	
	@Autowired
	CategoryService categoryService;

	@Autowired
	Utils utils;
	
	

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  getAllCategories() {
		LOGGER.info("getAllCategories invoked");
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = categoryService.getAllCategories();
		
		//entity = new ResponseEntity<Map<String, Object>>(resp, headers, rm.getStatus());
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
//	public String getCity(@PathVariable("id") int id) {
	public ResponseEntity<ResponseModel>  getCategory(@PathVariable("id") int id) {
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = categoryService.getCategory(id);
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/name/{name}", method = RequestMethod.GET)
//	public String getCity(@PathVariable("id") int id) {
	public ResponseEntity<ResponseModel>  getCityByName(@PathVariable("name") String name) {
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = categoryService.getCategoryByName(name);
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	

		
}
