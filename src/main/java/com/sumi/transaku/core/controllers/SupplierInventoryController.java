package com.sumi.transaku.core.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.domains.SupplierInventory;
import com.sumi.transaku.core.services.SupplierInventoryService;
import com.sumi.transaku.core.utils.Utils;

@Controller
@RequestMapping("/supplierInventory")
public class SupplierInventoryController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SupplierInventoryController.class);
	
	@Autowired
	SupplierInventoryService inventoryService;

	@Autowired
	Utils utils;
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseModel> add(@RequestBody SupplierInventory inventory) {
		LOGGER.info("addInventory invoked ");
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm = inventoryService.addInventory(inventory);
		
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<ResponseModel> update(@RequestBody SupplierInventory inventory) {
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm = inventoryService.updateInventory(inventory);		
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<ResponseModel> delete(@RequestBody SupplierInventory inventory) {
		ResponseEntity<ResponseModel> entity = null;
		
		//ResponseModel rm = customerService.updateCustomer(customerToDelete);		
		ResponseModel rm = inventoryService.deleteInventory(inventory);		
		
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}

	@RequestMapping(value = "/all/{supplierId}", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  getAllInventories(@PathVariable("supplierId") int supplierId) {
		LOGGER.info("getAllInventory invoked");
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = inventoryService.getAllInventoriesBySupplier(supplierId);
		
		//entity = new ResponseEntity<Map<String, Object>>(resp, headers, rm.getStatus());
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  getInventoryById(@PathVariable("id") int id) {
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = inventoryService.getInventory(id);
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/{supplierId}/{name}", method = RequestMethod.GET)
//	public String getCity(@PathVariable("id") int id) {
	public ResponseEntity<ResponseModel>  getInventoryByUpplierAndName(@PathVariable("supplierId") int supplierId, @PathVariable("name") String name) {
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = inventoryService.getInventoryBySupplierAndName(supplierId, name);
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/search/{name}", method = RequestMethod.GET)
//	public String getCity(@PathVariable("id") int id) {
	public ResponseEntity<ResponseModel>  getInventoryByName(@PathVariable("name") String name) {
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = inventoryService.getInventoryByName(name);
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/search/{name}/{sortType}", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  getInventoryByNameSortByPrice(@PathVariable("name") String name, @PathVariable("sortType") String sortType) {
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = inventoryService.getInventoryByNameOrderByPrice(name, sortType);
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	

}
