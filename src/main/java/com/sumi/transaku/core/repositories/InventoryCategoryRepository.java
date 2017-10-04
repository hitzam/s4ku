package com.sumi.transaku.core.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.sumi.transaku.core.domains.InventoryCategory;
import com.sumi.transaku.core.domains.StoreInventory;

public interface InventoryCategoryRepository extends CrudRepository<InventoryCategory, Integer>{

	List<InventoryCategory> findAll();
	StoreInventory findByName(String name);
	
}
