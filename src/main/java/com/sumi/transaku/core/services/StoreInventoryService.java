package com.sumi.transaku.core.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sumi.transaku.core.configs.GeneralConfig;
import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.domains.SellerCapital;
import com.sumi.transaku.core.domains.StoreInventory;
import com.sumi.transaku.core.domains.StoreInventoryRestockHistory;
import com.sumi.transaku.core.repositories.CustomerRepository;
import com.sumi.transaku.core.repositories.InventoryCategoryRepository;
import com.sumi.transaku.core.repositories.StoreInventoryRepository;
import com.sumi.transaku.core.utils.AwsS3Util;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;


@Service
public class StoreInventoryService {
	private static final Logger LOGGER = LoggerFactory.getLogger(StoreInventoryService.class);
	
	@Autowired
	StoreInventoryRepository inventoryRepository;
	
	@Autowired
	StoreInventoryRestockHistoryService historyService;

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	InventoryCategoryRepository categoryRepository;

	@Autowired
	SellerCapitalService capitalService;

	@Autowired
	Utils utils;
	
	@Autowired
	SupplierInventoryService supplierInventoryService;

	@Autowired
	GeneralConfig generalConfig;
	
	@Autowired
	AwsS3Util awsS3Util;
	
	public ResponseModel addInventory(StoreInventory inventory) {
		LOGGER.info("add Inventory");
		ResponseModel model = null;
		//inventory.setCode(utils.generateInventoryCode(inventory.getCustomer().getId()));
		inventory.setCode(utils.generateInventoryCode(inventory.getCustomerId()));
//		inventory.setCustomer(customerRepository.findOne(inventory.getCustomer().getId()));
		inventory.setSeller(customerRepository.findOne(inventory.getCustomerId()));
		inventory.setCreatedDate(new Date());
		
		//set inventory picture
		if(inventory.getPictureBase64()!=null && inventory.getPictureBase64().length()>10){
			//String decodedPicture = URLDecoder.decode(inventory.getPictureBase64(), "UTF-8");
			//LOGGER.info("after:\n"+decodedPicture);
			String decodedPicture = inventory.getPictureBase64();
			//inventory.setPicturePath(Utils.base64ToFile(decodedPicture, pathPicture, "picture_", inventory.getEmail()));
			
			String keyName = "store-inventory-picture-"+inventory.getCode()+".jpg";
			awsS3Util.uploadPicture(StaticFields.AWS_S3_STORE_INVENTORY_BUCKET,Utils.base64ToTmpFile(decodedPicture), keyName);
			inventory.setPicturePath(StaticFields.AWS_S3_STORE_INVENTORY_URL+keyName);
		}
		
		StoreInventory inventorySaved = inventoryRepository.save(inventory);
		System.out.println(inventorySaved);        
		
		if(inventorySaved != null){
			LOGGER.info(inventorySaved.toString());
			
			//if enabled, add store inv to supplier inv
			if(generalConfig.getConvertSellerSupplier() == 1){
				supplierInventoryService.convertStoreInvToSupplierInv(inventorySaved);
			}
			
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", inventorySaved.getId());
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to add inventory", null);
		}
		
		return model;

	}
	
	public ResponseModel updateInventory(StoreInventory inventory) {
		LOGGER.info("update Inventory");
		ResponseModel model = null;
		
		StoreInventory existingInventory = inventoryRepository.findOne(inventory.getId());
		
		existingInventory.setUpdatedDate(new Date());
		existingInventory.setName(inventory.getName());
		existingInventory.setDescription(inventory.getDescription());
		existingInventory.setPrice(inventory.getPrice());
		existingInventory.setBuyPrice(inventory.getBuyPrice());
		existingInventory.setQty(inventory.getQty());
		
		if(inventory.getPictureBase64()!=null && inventory.getPictureBase64().length()>10){
			//String decodedPicture = URLDecoder.decode(inventory.getPictureBase64(), "UTF-8");
			//LOGGER.info("after:\n"+decodedPicture);
			String decodedPicture = inventory.getPictureBase64();
			//inventory.setPicturePath(Utils.base64ToFile(decodedPicture, pathPicture, "picture_", inventory.getEmail()));
			
			String keyName = "store-inventory-picture-"+inventory.getCode()+".jpg";
			awsS3Util.uploadPicture(StaticFields.AWS_S3_STORE_INVENTORY_BUCKET,Utils.base64ToTmpFile(decodedPicture), keyName);
			inventory.setPicturePath(StaticFields.AWS_S3_STORE_INVENTORY_URL+keyName);
		}else{
			inventory.setPicturePath(existingInventory.getPicturePath());
		}
		
		StoreInventory inventoryUpdated = inventoryRepository.save(existingInventory);
		System.out.println(inventoryUpdated);        
		
		if(inventoryUpdated != null){
			LOGGER.info(inventoryUpdated.toString());
			
			//if enabled, add store inv to supplier inv
			if(generalConfig.getConvertSellerSupplier() == 1){
				supplierInventoryService.convertUpdateStoreInvToSupplierInv(inventoryUpdated);
			}
			
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", inventoryUpdated);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to update inventory", null);
		}
		
		return model;
	}
	
	public ResponseModel reStock(StoreInventory inventory) {
		LOGGER.info("reStock Inventory invoked");
		LOGGER.info(inventory.toString());
		ResponseModel model = null;
		
		//new item
		if(inventory.getId()==null || inventory.getId()<1){
			inventory.setCode(utils.generateInventoryCode(inventory.getCustomerId()));
			inventory.setSeller(customerRepository.findOne(inventory.getCustomerId()));
			inventory.setCreatedDate(new Date());
			inventory.setCategory(categoryRepository.findOne(inventory.getCategory().getId()));
			
			if(inventory.getPictureBase64()!=null && inventory.getPictureBase64().length()>10){
				//String decodedPicture = URLDecoder.decode(inventory.getPictureBase64(), "UTF-8");
				//LOGGER.info("after:\n"+decodedPicture);
				String decodedPicture = inventory.getPictureBase64();
				//inventory.setPicturePath(Utils.base64ToFile(decodedPicture, pathPicture, "picture_", inventory.getEmail()));
				
				String keyName = "store-inventory-picture-"+inventory.getCode()+".jpg";
				awsS3Util.uploadPicture(StaticFields.AWS_S3_STORE_INVENTORY_BUCKET,Utils.base64ToTmpFile(decodedPicture), keyName);
				inventory.setPicturePath(StaticFields.AWS_S3_STORE_INVENTORY_URL+keyName);
			}
			
			StoreInventory inventorySaved = inventoryRepository.save(inventory);
			System.out.println(inventorySaved);        
			
			if(inventorySaved != null){
				//update capital
				SellerCapital capital = new SellerCapital(inventorySaved.getSeller(), inventorySaved.getBuyPrice() * inventorySaved.getQty(), inventorySaved.getName());
				capitalService.updateCapitalByInventoryReStock(capital);
				
				//add restock history
				StoreInventoryRestockHistory history = new StoreInventoryRestockHistory(inventorySaved.getSeller().getId(), inventorySaved.getCode(), 
						inventorySaved.getBuyPrice(), inventorySaved.getPrice(), inventorySaved.getQty(), inventorySaved.getQty()*inventorySaved.getBuyPrice());
				historyService.addHistory(history);
				
				model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", inventorySaved.getId());
			}else{
				model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to restock inventory", null);
			}
		//update item	
		}else{
			StoreInventory existingInventory = inventoryRepository.findOne(inventory.getId());
			
			existingInventory.setUpdatedDate(new Date());
			existingInventory.setBuyPrice(inventory.getBuyPrice());
			existingInventory.setPrice(inventory.getPrice());
			existingInventory.setQty(existingInventory.getQty()+inventory.getQty());
			existingInventory.setDescription(inventory.getDescription());
			
			if(inventory.getPictureBase64()!=null && inventory.getPictureBase64().length()>10){
				//String decodedPicture = URLDecoder.decode(inventory.getPictureBase64(), "UTF-8");
				//LOGGER.info("after:\n"+decodedPicture);
				String decodedPicture = inventory.getPictureBase64();
				//inventory.setPicturePath(Utils.base64ToFile(decodedPicture, pathPicture, "picture_", inventory.getEmail()));
				
				String keyName = "store-inventory-picture-"+inventory.getCode()+".jpg";
				awsS3Util.uploadPicture(StaticFields.AWS_S3_STORE_INVENTORY_BUCKET,Utils.base64ToTmpFile(decodedPicture), keyName);
				existingInventory.setPicturePath(StaticFields.AWS_S3_STORE_INVENTORY_URL+keyName);
			}
			
			StoreInventory inventoryUpdated = inventoryRepository.save(existingInventory);
			System.out.println(inventoryUpdated);        
			
			if(inventoryUpdated != null){
				LOGGER.info(inventoryUpdated.toString());
				//update capital
				SellerCapital capital = new SellerCapital(inventoryUpdated.getSeller(), inventoryUpdated.getBuyPrice() * inventoryUpdated.getQty(), inventoryUpdated.getCode());
				capitalService.updateCapitalByInventoryReStock(capital);
				
				//add restock history
				StoreInventoryRestockHistory history = new StoreInventoryRestockHistory(inventoryUpdated.getSeller().getId(), inventoryUpdated.getCode(), 
						inventory.getBuyPrice(), inventory.getPrice(), inventory.getQty(), inventory.getQty()*inventory.getBuyPrice());
				historyService.addHistory(history);
				
				model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", inventoryUpdated.getId());
			}else{
				model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to restock inventory", null);
			}
		}
		return model;
	}

	public ResponseModel getRestockHostoryBySeller(int sellerId){
		LOGGER.info("get RestockHostory");
		ResponseModel model = null;
		List<StoreInventoryRestockHistory> histories = historyService.getHistoryBySeller(sellerId);
		
		if(histories != null){
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", histories);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getRestockHostoryBySellerAndDate(int sellerId, Date startDate, Date endDate){
		LOGGER.info("get RestockHostory");
		ResponseModel model = null;
		List<StoreInventoryRestockHistory> histories = historyService.getHistoryBySellerAndDate(sellerId, startDate, endDate);
		
		if(histories != null){
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", histories);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	
	public ResponseModel deleteInventory(StoreInventory inventory) {
		LOGGER.info("delete Inventory");
		ResponseModel model = null;
		
		//inventoryRepository.delete(inventory.getId());
		StoreInventory inv = inventoryRepository.findOne(inventory.getId());
		inv.setEnabled(StaticFields.STATUS_DISABLED);
		inventoryRepository.save(inv);
		
		model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", null);
		
		return model;
	}
	
	public ResponseModel getAllInventories(){
		LOGGER.info("get All Inventories");
		ResponseModel model = null;
		//List<StoreInventory> inventories = inventoryRepository.findAll();
		List<StoreInventory> inventories = inventoryRepository.findByEnabled(StaticFields.STATUS_ENABLED);
		
		if(inventories != null){
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", inventories);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getAllInventoriesBySeller(int id){
		LOGGER.info("get All Inventories");
		ResponseModel model = null;
		//List<StoreInventory> inventories = inventoryRepository.findBySeller(customerRepository.findOne(id));
		List<StoreInventory> inventories = inventoryRepository.findBySellerAndEnabledOrderByName(customerRepository.findOne(id), StaticFields.STATUS_ENABLED);
		
		if(inventories != null){
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", inventories);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getInventoriesToSellBySeller(int id){
		LOGGER.info("get Inventories ToSell By Seller");
		ResponseModel model = null;
		//List<StoreInventory> inventories = inventoryRepository.findBySellerAndIsItemToSell(customerRepository.findOne(id), StaticFields.STATUS_ENABLED);
		//List<StoreInventory> inventories = inventoryRepository.findBySellerAndIsItemToSellAndEnabledOrderByName(customerRepository.findOne(id), StaticFields.STATUS_ENABLED, StaticFields.STATUS_ENABLED);
		List<StoreInventory> inventories = inventoryRepository.findBySellerAndIsItemToSellAndEnabledAndQtyGreaterThanOrderByName(customerRepository.findOne(id), StaticFields.STATUS_ENABLED, StaticFields.STATUS_ENABLED, 0);
		
		if(inventories != null){
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", inventories);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getInventoriesBySellerAndName(int id, String name){
		LOGGER.info("get Inventories ToSell By Seller And Name");
		ResponseModel model = null;
		//List<StoreInventory> inventories = inventoryRepository.findBySellerAndNameContainingAndIsItemToSell(customerRepository.findOne(id), name, 1);
		List<StoreInventory> inventories = inventoryRepository.findBySellerAndNameContainingAndEnabledOrderByName(customerRepository.findOne(id), name, StaticFields.STATUS_ENABLED);
		
		if(inventories != null){
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", inventories);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getInventoriesToSellBySellerAndName(int id, String name){
		LOGGER.info("get Inventories ToSell By Seller And Name");
		ResponseModel model = null;
		//List<StoreInventory> inventories = inventoryRepository.findBySellerAndNameContainingAndIsItemToSell(customerRepository.findOne(id), name, 1);
		List<StoreInventory> inventories = inventoryRepository.findBySellerAndNameContainingAndIsItemToSellAndEnabledOrderByName(customerRepository.findOne(id), name, StaticFields.STATUS_ENABLED, StaticFields.STATUS_ENABLED);
		
		if(inventories != null){
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", inventories);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getInventory(int id){
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
		//List<StoreInventory> inventories = inventoryRepository.findByNameContaining(name);
		List<StoreInventory> inventories = inventoryRepository.findByNameContainingAndEnabled(name, StaticFields.STATUS_ENABLED);
		
		System.out.println(inventories);        
		
		if(inventories != null){
			LOGGER.info(inventories.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", inventories);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getInventoryBySellerAndName(int customerId, String name){
		LOGGER.info("get InventoryByCustomerAndName "+name);
		ResponseModel model = null;
		//List<StoreInventory> inventories = inventoryRepository.findBySellerAndNameContaining(customerRepository.findOne(customerId), name);
		//List<StoreInventory> inventories = inventoryRepository.findBySellerAndNameContainingAndEnabled(customerRepository.findOne(customerId), name, StaticFields.STATUS_ENABLED);
		List<StoreInventory> inventories = inventoryRepository.findBySellerAndNameContainingAndEnabledOrderByName(customerRepository.findOne(customerId), name, StaticFields.STATUS_ENABLED);
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
		//StoreInventory inventory = inventoryRepository.findByCode(code);
		StoreInventory inventory = inventoryRepository.findByCodeAndEnabled(code, StaticFields.STATUS_ENABLED);
		System.out.println(inventory);        
		
		if(inventory != null){
			LOGGER.info(inventory.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", inventory);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public void updateInventoryStock(Map<Integer, Double> itemsSold){
		LOGGER.info("updateInventoryStock "+itemsSold);
		itemsSold.forEach((id,qty)->{
			StoreInventory item = inventoryRepository.findOne(id);
			item.setQty(item.getQty()-qty);
			inventoryRepository.save(item); 
		});
		
	}
	
	
}
