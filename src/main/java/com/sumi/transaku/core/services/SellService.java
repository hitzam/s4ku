package com.sumi.transaku.core.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.domains.Sell;
import com.sumi.transaku.core.domains.SellDetail;
import com.sumi.transaku.core.domains.SellerCapital;
import com.sumi.transaku.core.domains.StoreInventory;
import com.sumi.transaku.core.domains.Supplier;
import com.sumi.transaku.core.repositories.CustomerRepository;
import com.sumi.transaku.core.repositories.SellDetailRepository;
import com.sumi.transaku.core.repositories.SellRepository;
import com.sumi.transaku.core.repositories.StoreInventoryRepository;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;


@Service
public class SellService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SellService.class);
	
	@Autowired
	StoreInventoryRepository inventoryRepository;

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	SellRepository sellRepository;
	
	@Autowired
	SellDetailRepository sellDetailRepository;
	
	@Autowired
	SellerCapitalService capitalService;
	
	@Autowired
	StoreInventoryService storeInventoryService;

	@Autowired
	Utils utils;
	
	public ResponseModel addSellTransaction(Sell sell) {
		LOGGER.info("add sell");
		ResponseModel model = null;
		Map<Integer, Double> itemsSold = new HashMap<>();
		sell.setId(null);
		sell.setCode(utils.generateSellCode(sell.getSeller().getId()));
		sell.setSeller(customerRepository.findOne(sell.getSeller().getId()));
		
		Set<SellDetail> details = sell.getSellDetails();
		for (SellDetail sellDetail : details) {
			sellDetail.setId(null);
			sellDetail.setSell(sell);
			sellDetail.setInventory(inventoryRepository.findOne(sellDetail.getInventory().getId()));
			
			LOGGER.info(sellDetail.toString());
			itemsSold.put(sellDetail.getInventory().getId(), sellDetail.getQty());
		}
		sell.setSellDetails(details);
		
		sell.setCreatedDate(new Date());
		LOGGER.info("before save: "+sell.toString());
		Sell sellSaved = sellRepository.save(sell);
		System.out.println(sellSaved);        
		
		if(sellSaved != null){
			LOGGER.info(sellSaved.toString());
			
			//update storeInventory
			storeInventoryService.updateInventoryStock(itemsSold);
			
			//update capital
			SellerCapital capital = new SellerCapital(sellSaved.getSeller(), sellSaved.getTotalPrice(), sellSaved);

			capitalService.updateCapitalByInventorySell(capital);
			
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", sellSaved.getId());
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to add sell", null);
		}
		
		return model;

	}
	
	public ResponseModel updateSell(Sell sell) {
		LOGGER.info("update sell");

		//delete old details
		List<SellDetail> existingSellDetails = sellDetailRepository.findBySell(sellRepository.findOne(sell.getId()));
		for(SellDetail detail : existingSellDetails){
			LOGGER.info("detail id to remove: {}",detail.getId());
			sellDetailRepository.delete(detail.getId());
		}
		
		ResponseModel model = null;
		Sell existingSell = sellRepository.findOne(sell.getId());
		Set<SellDetail> details = sell.getSellDetails();
		for (SellDetail sellDetail : details) {
			sellDetail.setSell(existingSell);
			sellDetail.setInventory(inventoryRepository.findOne(sellDetail.getInventory().getId()));
		}
		existingSell.setSellDetails(details);
		existingSell.setUpdatedDate(new Date());
		
		
		Sell sellUpdated = sellRepository.save(existingSell);
		System.out.println(sellUpdated);        
		
		if(sellUpdated != null){
			LOGGER.info(sellUpdated.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", sellUpdated.getId());
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to update sell", null);
		}
		
		return model;
	}
	
	/*public ResponseModel deleteInventory(StoreInventory inventory) {
		LOGGER.info("delete Inventory");
		ResponseModel model = null;
		
		inventoryRepository.delete(inventory.getId());
		
		model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", null);
		
		return model;
	}
	
	public ResponseModel getAllInventories(){
		ResponseModel model = null;
		List<StoreInventory> inventories = inventoryRepository.findAll();
		
		if(inventories != null){
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", inventories);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}*/
	
	public ResponseModel getSellByCustomerAndDateBetween(int id, Date startDate, Date endDate){
		ResponseModel model = null;
		List<Sell> sells = sellRepository.findBySellerAndCreatedDateBetween(customerRepository.findOne(id), startDate, endDate);
		
		double sellSumm = 0;
		if(sells != null){
			//sells.forEach(inv->{ inv.setSeller(null);});
			for (Sell sell : sells) {
				sell.setSeller(null);
				sellSumm += sell.getTotalPrice();
			}
			Map<String, String> addInfo = new HashMap<>();
			addInfo.put("total", String.valueOf(sellSumm));
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", addInfo, sells);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getItemSoldByCustomerAndDateBetween(int id, Date startDate, Date endDate){
		ResponseModel model = null;
		List<Sell> sells = sellRepository.findBySellerAndCreatedDateBetween(customerRepository.findOne(id), startDate, endDate);
		
		double sellSumm = 0;
		Map<Integer, Map<String, String>> nestedMap = new HashMap<>();
		Map<String, String> res = new HashMap<>();
		
		if(sells != null){
			for (Sell sell : sells) {
				for (SellDetail detail : sell.getSellDetails()){
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
	
	/*public ResponseModel getInventory(int id){
		LOGGER.info("get Inventory");
		ResponseModel model = null;
		StoreInventory inventory = inventoryRepository.findOne(id);
		System.out.println(inventory);        
		
		if(inventory != null){
			LOGGER.info(inventory.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", inventory);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getInventoryByName(String name){
		LOGGER.info("get InventoryByName "+name);
		ResponseModel model = null;
		List<StoreInventory> inventories = inventoryRepository.findByNameContaining(name);
		
		System.out.println(inventories);        
		
		if(inventories != null){
			LOGGER.info(inventories.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", inventories);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getInventoryByCustomerAndName(int customerId, String name){
		LOGGER.info("get InventoryByCustomerAndName "+name);
		ResponseModel model = null;
		List<StoreInventory> inventories = inventoryRepository.findBySellerAndNameContaining(customerRepository.findOne(customerId), name);
		System.out.println(inventories);        
		
		if(inventories != null){
			LOGGER.info(inventories.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", inventories);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getInventoryByCode(String code){
		LOGGER.info("get InventoryByCode "+code);
		ResponseModel model = null;
		StoreInventory inventory = inventoryRepository.findByCode(code);
		System.out.println(inventory);        
		
		if(inventory != null){
			LOGGER.info(inventory.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", inventory);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	*/
	
}
