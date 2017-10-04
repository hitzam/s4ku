package com.sumi.transaku.core.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.sumi.transaku.core.domains.InventoryCategory;

//@Transactional
public interface CategoryRepository extends CrudRepository<InventoryCategory, Integer>{

	List<InventoryCategory> findAll();
	InventoryCategory findByName(String name);
	
}
