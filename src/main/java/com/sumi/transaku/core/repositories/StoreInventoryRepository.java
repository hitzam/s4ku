package com.sumi.transaku.core.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.sumi.transaku.core.domains.Customer;
import com.sumi.transaku.core.domains.StoreInventory;

public interface StoreInventoryRepository extends CrudRepository<StoreInventory, Integer>{

	List<StoreInventory> findAll();
	List<StoreInventory> findByEnabled(int enabled);
	List<StoreInventory> findBySeller(Customer seller);
	List<StoreInventory> findBySellerAndEnabled(Customer seller, int enabled);
	List<StoreInventory> findBySellerAndEnabledOrderByName(Customer seller, int enabled);
	List<StoreInventory> findBySellerAndIsItemToSell(Customer seller, int isItemToSell);
	List<StoreInventory> findBySellerAndIsItemToSellAndEnabled(Customer seller, int isItemToSell, int enabled);
	List<StoreInventory> findBySellerAndIsItemToSellAndEnabledOrderByName(Customer seller, int isItemToSell, int enabled);
	List<StoreInventory> findBySellerAndIsItemToSellAndEnabledAndQtyGreaterThanOrderByName(Customer seller, int isItemToSell, int enabled, double qty);
	List<StoreInventory> findBySellerAndNameContaining(Customer seller, String name);
	List<StoreInventory> findBySellerAndNameContainingAndEnabled(Customer seller, String name, int enabled);
	List<StoreInventory> findBySellerAndNameContainingAndEnabledOrderByName(Customer seller, String name, int enabled);
	List<StoreInventory> findBySellerAndNameContainingAndIsItemToSell(Customer seller, String name, int isItemToSell);
	List<StoreInventory> findBySellerAndNameContainingAndIsItemToSellAndEnabled(Customer seller, String name, int isItemToSell, int enabled);
	List<StoreInventory> findBySellerAndNameContainingAndIsItemToSellAndEnabledOrderByName(Customer seller, String name, int isItemToSell, int enabled);
	List<StoreInventory> findByNameContaining(String name);
	List<StoreInventory> findByNameContainingAndEnabled(String name, int enabled);
	List<StoreInventory> findByNameContainingAndEnabledOrderByName(String name, int enabled);
	List<StoreInventory> findByNameContainingAndIsItemToSell(String name, int isItemToSell);
	List<StoreInventory> findByNameContainingAndIsItemToSellAndEnabled(String name, int isItemToSell, int enabled);
	StoreInventory findByCode(String code);
	StoreInventory findByCodeAndEnabled(String code, int enabled);
	StoreInventory findTopByOrderByIdDesc();
	
}
