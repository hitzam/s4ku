package com.sumi.transaku.core.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.domains.SupplierPurchaseOrder;
import com.sumi.transaku.core.domains.SupplierPurchaseOrderDetail;
import com.sumi.transaku.core.domains.User;
import com.sumi.transaku.core.repositories.CustomerRepository;
import com.sumi.transaku.core.repositories.SupplierInventoryRepository;
import com.sumi.transaku.core.repositories.SupplierPurchaseOrderDetailRepository;
import com.sumi.transaku.core.repositories.SupplierPurchaseOrderRepository;
import com.sumi.transaku.core.repositories.SupplierRepository;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;


@Service
public class SupplierPurchaseOrderService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SupplierPurchaseOrderService.class);
	
	@Autowired
	SupplierInventoryRepository inventoryRepository;

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	SupplierRepository supplierRepository;
	
	@Autowired
	SupplierPurchaseOrderRepository orderRepository;
	
	@Autowired
	SupplierPurchaseOrderDetailRepository orderDetailRepository;
	
	@Autowired
	SellerCapitalService capitalService;
	
	@Autowired
	SupplierInventoryService supplierInventoryService;

	@Autowired
	Utils utils;
	
	public ResponseModel addOrder(SupplierPurchaseOrder po) {
		LOGGER.info("add order");
		ResponseModel model = null;
		Map<Integer, Double> itemsSold = new HashMap<>();
		po.setId(null);
		po.setCode(utils.generatePurchaseOrderCode(po.getSupplier().getId()));
		po.setSupplier(supplierRepository.findOne(po.getSupplier().getId()));
		po.setStatus(StaticFields.STATUS_PURCHASE_ORDER_REGISTERED);
		
		Set<SupplierPurchaseOrderDetail> details = po.getOrderDetails();
		for (SupplierPurchaseOrderDetail poDetail : details) {
			poDetail.setId(null);
			poDetail.setSupplierPurchaseOrder(po);
			poDetail.setInventory(inventoryRepository.findOne(poDetail.getInventory().getId()));
			
			LOGGER.info(poDetail.toString());
			itemsSold.put(poDetail.getInventory().getId(), poDetail.getQty());
		}
		po.setOrderDetails(details);
		
		po.setCreatedDate(new Date());
		//LOGGER.info("before save: "+po.toString());
		SupplierPurchaseOrder poSaved = orderRepository.save(po);
		//System.out.println(poSaved);        
		
		if(poSaved != null){
			LOGGER.info(poSaved.toString());
			
			//update storeInventory
			//supplierInventoryService.updateInventoryStock(itemsSold);
			
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", poSaved.getId());
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to add purchase order", null);
		}
		
		return model;

	}
	
	public ResponseModel updateOrder(SupplierPurchaseOrder po) {
		LOGGER.info("update po");
		
		//delete old details
		List<SupplierPurchaseOrderDetail> existingPoDetails = orderDetailRepository.findBySupplierPurchaseOrder(orderRepository.findOne(po.getId()));
		for(SupplierPurchaseOrderDetail detail : existingPoDetails){
			LOGGER.info("detail id to remove: {}",detail.getId());
			orderDetailRepository.delete(detail.getId());
		}
		
		ResponseModel model = null;
		SupplierPurchaseOrder existingPo = orderRepository.findOne(po.getId());
		Set<SupplierPurchaseOrderDetail> details = po.getOrderDetails();
		for (SupplierPurchaseOrderDetail sellDetail : details) {
			sellDetail.setSupplierPurchaseOrder(existingPo);
			sellDetail.setInventory(inventoryRepository.findOne(sellDetail.getInventory().getId()));
		}
		existingPo.setOrderDetails(details);
		existingPo.setUpdatedDate(new Date());
		
		
		SupplierPurchaseOrder poUpdated = orderRepository.save(existingPo);
		System.out.println(poUpdated);        
		
		if(poUpdated != null){
			LOGGER.info(poUpdated.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", poUpdated.getId());
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to update purchase order", null);
		}
		
		return model;
	}
	
	public ResponseModel updateOrderStatus(int poId, int status, User updatedBy) {
		String strStatus = "";
		if(status==StaticFields.STATUS_PURCHASE_ORDER_SENT_TO_SUPPLIER)
			strStatus = "PURCHASE_ORDER_SENT_TO_SUPPLIER";
		else if(status==StaticFields.STATUS_PURCHASE_ORDER_ON_PROCESS)
			strStatus = "PURCHASE_ORDER_ON_PROCESS";
		else if(status==StaticFields.STATUS_PURCHASE_ORDER_FINISHED)
			strStatus = "PURCHASE_ORDER_FINISHED";
		
		LOGGER.info("update po {} status as {}", poId, strStatus);
		ResponseModel model = null;

		//delete old details
		SupplierPurchaseOrder existingPo = orderRepository.findOne(poId);
		existingPo.setStatus(status);
		existingPo.setUpdatedBy(updatedBy);
		existingPo.setUpdatedDate(new Date());
		
		SupplierPurchaseOrder poUpdated = orderRepository.save(existingPo);
		System.out.println(poUpdated);        
		
		if(poUpdated != null){
			LOGGER.info(poUpdated.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", poUpdated.getId());
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to update purchase order status", null);
		}
		
		return model;
	}
	
	//Get Unfinished Order
	public ResponseModel getActiveOrders(){
		ResponseModel model = null;
		List<Integer> inactiveStatus = new ArrayList<>();
		inactiveStatus.add(StaticFields.STATUS_PURCHASE_ORDER_CANCELED);
		inactiveStatus.add(StaticFields.STATUS_PURCHASE_ORDER_FINISHED);
		
		List<SupplierPurchaseOrder> orders = orderRepository.findByStatusNotIn(inactiveStatus);
		
		
		if(orders != null){
			
			//Hide Customer Inventories
			orders.forEach(order->{
				order.getCustomer().setStoreInventories(null);
			});
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", orders);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}

	public ResponseModel getOrderBySupplierAndDateBetween(int id, Date startDate, Date endDate){
		ResponseModel model = null;
		List<SupplierPurchaseOrder> orders = orderRepository.findBySupplierAndCreatedDateBetween(supplierRepository.findOne(id), startDate, endDate);
		
		double sellSumm = 0;
		if(orders != null){
			//sells.forEach(inv->{ inv.setSeller(null);});
			for (SupplierPurchaseOrder order : orders) {
				order.setSupplier(null);
				sellSumm += order.getTotalPrice();
			}
			Map<String, String> addInfo = new HashMap<>();
			addInfo.put("total", String.valueOf(sellSumm));
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", addInfo, orders);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getOrderByCustomerAndDateBetween(int id, Date startDate, Date endDate){
		ResponseModel model = null;
		List<SupplierPurchaseOrder> orders = orderRepository.findByCustomerAndCreatedDateBetween(customerRepository.findOne(id), startDate, endDate);
		
		double sellSumm = 0;
		if(orders != null){
			//sells.forEach(inv->{ inv.setSeller(null);});
			for (SupplierPurchaseOrder order : orders) {
				order.setSupplier(null);
				order.getOrderDetails().forEach(detail->{detail.getInventory().setSupplier(null);});
				order.getCustomer().setStoreInventories(null);
				sellSumm += order.getTotalPrice();
			}
			Map<String, String> addInfo = new HashMap<>();
			addInfo.put("total", String.valueOf(sellSumm));
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", addInfo, orders);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getItemOrderedBySupplierAndDateBetween(int id, Date startDate, Date endDate){
		ResponseModel model = null;
		List<SupplierPurchaseOrder> orders = orderRepository.findBySupplierAndCreatedDateBetween(supplierRepository.findOne(id), startDate, endDate);
		
		double sellSumm = 0;
		Map<Integer, Map<String, String>> nestedMap = new HashMap<>();
		Map<String, String> res = new HashMap<>();
		
		if(orders != null){
			for (SupplierPurchaseOrder order : orders) {
				for (SupplierPurchaseOrderDetail detail : order.getOrderDetails()){
					if(nestedMap.size() < 1){
						Map<String, String> map = new HashMap<>();
						map.put(detail.getInventory().getName(), detail.getQty()+" "+detail.getInventory().getQtyUnit().getName());
						nestedMap.put(detail.getInventory().getId(), map);
					}else{
						if(nestedMap.containsKey(detail.getInventory().getId())){
							Double lastQty = Double.valueOf(nestedMap.get(detail.getInventory().getId()).get(detail.getInventory().getName()).split(" ")[0]);
							Map<String, String> map = new HashMap<>();
							map.put(detail.getInventory().getName(), (lastQty+detail.getQty())+" "+detail.getInventory().getQtyUnit().getName());
							nestedMap.put(detail.getInventory().getId(), map);
						}else{
							Map<String, String> map = new HashMap<>();
							map.put(detail.getInventory().getName(), detail.getQty()+" "+detail.getInventory().getQtyUnit().getName());
							nestedMap.put(detail.getInventory().getId(), map);
						}
					}
				}
			}

			for(Entry<Integer, Map<String, String>> e : nestedMap.entrySet()){
				for(Entry<String, String> en : e.getValue().entrySet()){
					res.put(en.getKey(), en.getValue());
				}
			}
			
			Map<String, String> addInfo = new HashMap<>();
			addInfo.put("total", String.valueOf(sellSumm));
			//model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", addInfo, sells);
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", res);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	
}
