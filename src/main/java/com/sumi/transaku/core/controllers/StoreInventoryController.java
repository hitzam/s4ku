package com.sumi.transaku.core.controllers;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.websocket.server.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

import com.sumi.transaku.core.domains.City;
import com.sumi.transaku.core.domains.Customer;
import com.sumi.transaku.core.domains.Province;
import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.domains.StoreInventory;
import com.sumi.transaku.core.services.CityService;
import com.sumi.transaku.core.services.StoreInventoryService;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;

@Controller
@RequestMapping("/inventory")
public class StoreInventoryController {
	private static final Logger LOGGER = LoggerFactory.getLogger(StoreInventoryController.class);
	
	@Autowired
	StoreInventoryService inventoryService;

	@Autowired
	Utils utils;
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseModel> add(@RequestBody StoreInventory inventory) {
		LOGGER.info("addInventory invoked ");
		ResponseEntity<ResponseModel> entity = null;
		
		inventory.setEnabled(StaticFields.STATUS_ENABLED);
		ResponseModel rm = inventoryService.addInventory(inventory);
		
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<ResponseModel> update(@RequestBody StoreInventory inventory) {
		ResponseEntity<ResponseModel> entity = null;

		inventory.setEnabled(StaticFields.STATUS_ENABLED);
		ResponseModel rm = inventoryService.updateInventory(inventory);		
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "/reStock", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseModel> reStock(@RequestBody StoreInventory inventory) {
		ResponseEntity<ResponseModel> entity = null;

		inventory.setEnabled(StaticFields.STATUS_ENABLED);
		ResponseModel rm = inventoryService.reStock(inventory);		
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "/reStockHistory/{sellerId}/{startDate}/{endDate}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<ResponseModel> reStockHistory(@PathVariable("sellerId")Integer sellerId, @PathVariable("startDate")String startDate, @PathVariable("endDate")String endDate) {
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm;
		try {
			rm = inventoryService.getRestockHostoryBySellerAndDate(sellerId, utils.sdf.parse(startDate), utils.sdf.parse(endDate));
			entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		} catch (ParseException e) {
			e.printStackTrace();
		}		
		
		return entity;
	}
	
	
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<ResponseModel> delete(@RequestBody StoreInventory inventory) {
		ResponseEntity<ResponseModel> entity = null;
		
		//ResponseModel rm = customerService.updateCustomer(customerToDelete);		
		ResponseModel rm = inventoryService.deleteInventory(inventory);		
		
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}

	@RequestMapping(value = "/all/{sellerId}", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  getAllInventories(@PathVariable("sellerId") int sellerId) {
		LOGGER.info("getAllCities invoked");
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = inventoryService.getAllInventoriesBySeller(sellerId);
		
		//entity = new ResponseEntity<Map<String, Object>>(resp, headers, rm.getStatus());
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/toSell/{sellerId}", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  getInventoriesToSell(@PathVariable("sellerId") int sellerId) {
		LOGGER.info("getAllCities invoked");
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = inventoryService.getInventoriesToSellBySeller(sellerId);
		
		//entity = new ResponseEntity<Map<String, Object>>(resp, headers, rm.getStatus());
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
//	public String getCity(@PathVariable("id") int id) {
	public ResponseEntity<ResponseModel>  getInventoryById(@PathVariable("id") int id) {
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = inventoryService.getInventory(id);
		
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
	
	@RequestMapping(value = "/search/{sellerId}/{name}", method = RequestMethod.GET)
//	public String getCity(@PathVariable("id") int id) {
	public ResponseEntity<ResponseModel>  getInventoryBySellerAndName(@PathVariable("sellerId") int sellerId, @PathVariable("name") String name) {
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = inventoryService.getInventoryBySellerAndName(sellerId, name);
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/search/toSell/{sellerId}/{name}", method = RequestMethod.GET)
//	public String getCity(@PathVariable("id") int id) {
	public ResponseEntity<ResponseModel>  getInventoriesToSellBySellerAndName(@PathVariable("sellerId") int sellerId, @PathVariable("name") String name) {
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = inventoryService.getInventoriesToSellBySellerAndName(sellerId, name);
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	

}
