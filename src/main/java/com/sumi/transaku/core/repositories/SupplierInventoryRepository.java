package com.sumi.transaku.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.sumi.transaku.core.domains.Supplier;
import com.sumi.transaku.core.domains.SupplierInventory;

public interface SupplierInventoryRepository extends CrudRepository<SupplierInventory, Integer>{

	List<SupplierInventory> findAll();
	List<SupplierInventory> findBySupplier(Supplier supplier);
	List<SupplierInventory> findBySupplierAndNameContaining(Supplier supplier, String name);
	List<SupplierInventory> findByNameContaining(String name);
	List<SupplierInventory> findByNameContainingOrderByPriceAsc(String name);
	List<SupplierInventory> findByNameContainingOrderByPriceDesc(String name);
	SupplierInventory findByCode(String code);
	SupplierInventory findTopByOrderByIdDesc();
	
	/*NATIVE QUERY*/
	@Query("SELECT new com.sumi.transaku.core.domains.SupplierInventory(si.id, si.name, si.supplier) "
			+ "FROM SupplierInventory si WHERE si.name like %?1%")
	List<SupplierInventory> getInventoriesByNameNative(String name);
}
