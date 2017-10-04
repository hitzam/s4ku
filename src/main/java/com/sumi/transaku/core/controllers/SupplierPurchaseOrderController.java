package com.sumi.transaku.core.controllers;

import java.text.ParseException;

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

import com.sumi.transaku.core.configs.MailConfiguration;
import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.domains.SupplierPurchaseOrder;
import com.sumi.transaku.core.services.CustomerService;
import com.sumi.transaku.core.services.MailService;
import com.sumi.transaku.core.services.SupplierPurchaseOrderService;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;

@Controller
@RequestMapping("/supplierPO")
public class SupplierPurchaseOrderController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SupplierPurchaseOrderController.class);
	
	@Autowired
	CustomerService customerService;

	@Autowired
	SupplierPurchaseOrderService orderService;

	@Autowired
	MailConfiguration mailConfig;

	@Autowired
	MailService mailService;

	@Autowired
	Utils utils;
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseModel> add(@RequestBody SupplierPurchaseOrder order) {
		LOGGER.info("po for: "+order.getSupplier().getId());
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm = orderService.addOrder(order);
		
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<ResponseModel> update(@RequestBody SupplierPurchaseOrder order) {
		LOGGER.info("update order for: "+order.getSupplier().getId());
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm = orderService.updateOrder(order);		
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseModel> updateStatus(@RequestBody SupplierPurchaseOrder order) {
		LOGGER.info("update order status {} as {} by {}", order.getCode(),order.getStatus(), order.getUpdatedBy().getName());
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm = orderService.updateOrderStatus(order.getId(), order.getStatus(), order.getUpdatedBy());		
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "/active", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<ResponseModel> getActiveOrders() {
		LOGGER.info("getActiveOrders invoked");
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm;
		rm = orderService.getActiveOrders();
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "/history/{supplierId}/{startDate}/{endDate}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<ResponseModel> history(@PathVariable("supplierId")int supplierId, @PathVariable("startDate")String startDate, @PathVariable("endDate")String endDate) {
		LOGGER.info("history order for: {} {} - {}", supplierId, startDate, endDate);
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm;
		try {
			rm = orderService.getOrderBySupplierAndDateBetween(supplierId, utils.sdhmf.parse(startDate), utils.sdhmf.parse(endDate));
			entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		} catch (ParseException e) {
			rm = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_FAILED, "failed to retrieve data", null);
			entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
			e.printStackTrace();
		}		
		
		return entity;
	}
	
	@RequestMapping(value = "/itemOrderedSummary/{supplierId}/{startDate}/{endDate}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<ResponseModel> itemSoldSummary(@PathVariable("supplierId")int supplierId, @PathVariable("startDate")String startDate, @PathVariable("endDate")String endDate) {
		LOGGER.info("item Ordered Summary invoked for: {} {} - {}", supplierId, startDate, endDate);
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm;
		try {
			rm = orderService.getItemOrderedBySupplierAndDateBetween(supplierId, utils.sdhmf.parse(startDate), utils.sdhmf.parse(endDate));
			entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		} catch (ParseException e) {
			rm = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_FAILED, "failed to retrieve data", null);
			entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
			e.printStackTrace();
		}		
		
		return entity;
	}
	
	@RequestMapping(value = "/customerHistory/{customerId}/{startDate}/{endDate}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<ResponseModel> customerHistory(@PathVariable("customerId")int customerId, @PathVariable("startDate")String startDate, @PathVariable("endDate")String endDate) {
		LOGGER.info("customerHistory order for: {} {} - {}", customerId, startDate, endDate);
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm;
		try {
			rm = orderService.getOrderByCustomerAndDateBetween(customerId, utils.sdhmf.parse(startDate), utils.sdhmf.parse(endDate));
			entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		} catch (ParseException e) {
			rm = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_FAILED, "failed to retrieve data", null);
			entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
			e.printStackTrace();
		}		
		
		return entity;
	}
	
	
}
