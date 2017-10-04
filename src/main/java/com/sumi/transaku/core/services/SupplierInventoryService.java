package com.sumi.transaku.core.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sumi.transaku.core.configs.GeneralConfig;
import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.domains.StoreInventory;
import com.sumi.transaku.core.domains.Supplier;
import com.sumi.transaku.core.domains.SupplierInventory;
import com.sumi.transaku.core.repositories.InventoryCategoryRepository;
import com.sumi.transaku.core.repositories.SupplierInventoryRepository;
import com.sumi.transaku.core.repositories.SupplierRepository;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;


@Service
public class SupplierInventoryService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SupplierInventoryService.class);
	
	@Autowired
	SupplierInventoryRepository inventoryRepository;
	
//	@Autowired
//	CustomerRepository customerRepository;

	@Autowired
	SupplierRepository supplierRepository;

	@Autowired
	SupplierService supplierService;
	
	@Autowired
	InventoryCategoryRepository categoryRepository;

	@Autowired
	SellerCapitalService capitalService;

	@Autowired
	Utils utils;

	
	public ResponseModel addInventory(SupplierInventory inventory) {
		LOGGER.info("add Inventory");
		ResponseModel model = null;
		inventory.setCode(utils.generateSupplierInventoryCode(inventory.getSupplier().getId()));
		inventory.setSupplier(supplierRepository.findOne(inventory.getSupplier().getId()));
		inventory.setCreatedDate(new Date());
		SupplierInventory inventorySaved = inventoryRepository.save(inventory);
		System.out.println(inventorySaved);        
		
		if(inventorySaved != null){
			LOGGER.info(inventorySaved.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", inventorySaved.getId());
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to add inventory", null);
		}
		
		return model;

	}
	
	public ResponseModel updateInventory(SupplierInventory inventory) {
		LOGGER.info("update Inventory");
		ResponseModel model = null;
		
		SupplierInventory existingInventory = inventoryRepository.findOne(inventory.getId());
		
		existingInventory.setUpdatedDate(new Date());
		existingInventory.setName(inventory.getName());
		existingInventory.setDescription(inventory.getDescription());
		existingInventory.setPrice(inventory.getPrice());;
		existingInventory.setQty(inventory.getQty());
		existingInventory.setWeight(inventory.getWeight());
		
		SupplierInventory inventoryUpdated = inventoryRepository.save(existingInventory);
		System.out.println(inventoryUpdated);        
		
		if(inventoryUpdated != null){
			LOGGER.info(inventoryUpdated.toString());
			//update capital
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", inventoryUpdated);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to update inventory", null);
		}
		
		return model;
	}
	
	public ResponseModel deleteInventory(SupplierInventory inventory) {
		LOGGER.info("delete Inventory");
		ResponseModel model = null;
		
		inventoryRepository.delete(inventory.getId());
		
		model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", null);
		
		return model;
	}
	
	public ResponseModel getAllInventories(){
		LOGGER.info("get All Inventories");
		ResponseModel model = null;
		List<SupplierInventory> inventories = inventoryRepository.findAll();
		
		if(inventories != null){
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", inventories);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getAllInventoriesBySupplier(int id){
		LOGGER.info("get All Inventories BySupplier");
		ResponseModel model = null;
		List<SupplierInventory> inventories = inventoryRepository.findBySupplier(supplierRepository.findOne(id));
		
		if(inventories != null){
			inventories.forEach(inventory->{
				inventory.setSupplier(null);
			});
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", inventories);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getInventory(int id){
		LOGGER.info("get Inventory");
		ResponseModel model = null;
		SupplierInventory inventory = inventoryRepository.findOne(id);
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
		List<SupplierInventory> inventories = inventoryRepository.findByNameContaining(name);
		
		System.out.println(inventories);        
		
		if(inventories != null){
			LOGGER.info(inventories.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", inventories);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getInventoryByNameOrderByPrice(String name, String sortType){
		LOGGER.info("get getInventoryByNameOrderByPrice "+name);
		ResponseModel model = null;
		List<SupplierInventory> inventories = null;
		if(sortType.equalsIgnoreCase("asc"))
			inventories = inventoryRepository.findByNameContainingOrderByPriceAsc(name);
		else if(sortType.equalsIgnoreCase("desc"))
			inventories = inventoryRepository.findByNameContainingOrderByPriceDesc(name);
		
		System.out.println(inventories);        
		
		if(inventories != null){
			LOGGER.info(inventories.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", inventories);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getInventoryByNameNative(String name){
		LOGGER.info("get InventoryByName "+name);
		ResponseModel model = null;
		List<SupplierInventory> inventories = inventoryRepository.getInventoriesByNameNative(name);
		
		//to avoid duplicates
		Set<Supplier> suppliers = new HashSet<>();
		List<Supplier> supplierList = new ArrayList<>();
		
		System.out.println(inventories);        
		
		if(inventories != null){
			LOGGER.info(inventories.toString());
			for (SupplierInventory supplierInventory : inventories) {
				Supplier sup = supplierInventory.getSupplier();
				sup.setPassword(null);
				suppliers.add(sup);
			}
			
			for (Supplier supplier : suppliers) {
				supplierList.add(supplier);
			}
			
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", supplierList);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getInventoryByNameNative(String name, Double latitude, Double longitude){
		LOGGER.info("get InventoryByName "+name);
		ResponseModel model = null;
		List<SupplierInventory> inventories = inventoryRepository.getInventoriesByNameNative(name);

		//to avoid duplicates
		Set<Supplier> suppliers = new HashSet<>();
		List<Supplier> supplierList = new ArrayList<>();
		
		
		System.out.println(inventories);        
		
		if(inventories != null){
			LOGGER.info(inventories.toString());
			for (SupplierInventory supplierInventory : inventories) {
				Supplier sup = supplierInventory.getSupplier();
				sup.setPassword(null);
				suppliers.add(sup);
			}
			for (Supplier supplier : suppliers) {
				supplierList.add(supplier);
			}
			
			//TODO: TEST SORTING
        	if( latitude != null && longitude != null)
        		supplierList = supplierService.sortSupplierByNearestLocation(supplierList, latitude, longitude);
        	
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", supplierList);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	/*public List<SupplierInventory> getInventoryByNameNative(String name){
		LOGGER.info("get InventoryByName2 "+name);
		ResponseModel model = null;
		return inventoryRepository.getInventoriesByNameNative(name);
	}*/
	
	public ResponseModel getInventoryBySupplierAndName(int supplierId, String name){
		LOGGER.info("get InventoryBySupplierAndName "+name);
		ResponseModel model = null;
		List<SupplierInventory> inventories = inventoryRepository.findBySupplierAndNameContaining(supplierRepository.findOne(supplierId), name);
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
		SupplierInventory inventory = inventoryRepository.findByCode(code);
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
			SupplierInventory item = inventoryRepository.findOne(id);
			item.setQty(item.getQty()-qty);
			inventoryRepository.save(item); 
		});
		
	}
	
	public void convertStoreInvToSupplierInv(StoreInventory storeInv){
		LOGGER.info("convertStoreInvToSupplierInv for seller {}", storeInv.getSeller().getEmail());
		Supplier sup = supplierRepository.findByEmail(storeInv.getSeller().getEmail());
		if(sup != null){
			SupplierInventory supplierInventory = new SupplierInventory(storeInv.getCode(), storeInv.getCategory(), sup, storeInv.getName(), storeInv.getDescription(), storeInv.getPrice(), storeInv.getQty(), storeInv.getQtyUnit(), storeInv.getWeight(), storeInv.getCreatedDate(), storeInv.getNote());
			inventoryRepository.save(supplierInventory);
		}else{
			LOGGER.info("supplier with email {} doesn't exist", storeInv.getSeller().getEmail());
		}
	}
	
	public void convertUpdateStoreInvToSupplierInv(StoreInventory storeInv){
		LOGGER.info("convertStoreInvToSupplierInv for seller {}", storeInv.getSeller().getEmail());
		SupplierInventory supInv = inventoryRepository.findByCode(storeInv.getCode());
		
		if(supInv != null){
			supInv.setCategory(storeInv.getCategory());
			supInv.setName(storeInv.getName());
			supInv.setDescription(storeInv.getDescription());
			supInv.setPrice(storeInv.getPrice());
			supInv.setQty(storeInv.getQty());
			supInv.setQtyUnit(storeInv.getQtyUnit());
			supInv.setWeight(storeInv.getWeight());
			supInv.setNote(storeInv.getNote());
			inventoryRepository.save(supInv);
		}else{
			LOGGER.info("supplier inventory with code {} doesn't exist", storeInv.getCode());
		}
	}
	
}
