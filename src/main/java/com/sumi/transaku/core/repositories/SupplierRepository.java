package com.sumi.transaku.core.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import com.sumi.transaku.core.domains.City;
import com.sumi.transaku.core.domains.Supplier;
import com.sumi.transaku.core.domains.SupplierInventory;

//@Transactional
public interface SupplierRepository extends CrudRepository<Supplier, Integer>{

//	List<Supplier> findByNamaLengkapContainingIgnoreCase(String namaLengkap);
	List<Supplier> findAll();
	List<Supplier> findByStatus(int status);
	List<Supplier> findByStatusGreaterThan(int status);
	List<Supplier> findByNameContaining(String name);
	List<Supplier> findByNameContainingAndStatus(String name, int status);
	List<Supplier> findByNameContainingAndStatusGreaterThan(String name, int status);

	Supplier findByName(String name);
	Supplier findByEmail(String email);
	Supplier findByEmailAndStatus(String email, int status);
	Supplier findByPhone(String phone);
	Supplier findByPhoneAndStatus(String phone, int status);
	Supplier findBySupplierInventories(Set<SupplierInventory> supplierInventory);
	
	long countByCity(City city);
	long countByStatus(int status);
	
	/*NATIVE QUERY*/
	/*@Query("SELECT new com.sumi.transaku.core.domains.Supplier(c.id, c.name, c.email, c.password, c.gender, c.phone, c.isVerified, c.status, c.enabled, c.storeInventories) "
			+ "FROM Supplier c WHERE c.id = ?1")
	Supplier getCustomerProfile(int id);*/
	
}
