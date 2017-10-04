package com.sumi.transaku.core.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
/*import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;*/
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.services.QtyUnitService;
import com.sumi.transaku.core.utils.Utils;

@Controller
@RequestMapping("/qtyUnit")
public class QtyUnitController {
	private static final Logger LOGGER = LoggerFactory.getLogger(QtyUnitController.class);
	
	@Autowired
	QtyUnitService unitService;

	@Autowired
	Utils utils;
	
	

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  getAllQtyUnits() {
		LOGGER.info("getAllQtyUnits invoked");
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = unitService.getAllUnits();
		
		//entity = new ResponseEntity<Map<String, Object>>(resp, headers, rm.getStatus());
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
//	public String getCity(@PathVariable("id") int id) {
	public ResponseEntity<ResponseModel>  getCategory(@PathVariable("id") int id) {
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = unitService.getUnit(id);
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/name/{name}", method = RequestMethod.GET)
//	public String getCity(@PathVariable("id") int id) {
	public ResponseEntity<ResponseModel>  getCityByName(@PathVariable("name") String name) {
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = unitService.getUnitByName(name);
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	

		
}
