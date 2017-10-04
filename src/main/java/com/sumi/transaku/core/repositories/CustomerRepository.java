package com.sumi.transaku.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.sumi.transaku.core.domains.City;
import com.sumi.transaku.core.domains.Customer;

//@Transactional
public interface CustomerRepository extends CrudRepository<Customer, Integer>{

//	List<Customer> findByNamaLengkapContainingIgnoreCase(String namaLengkap);
	List<Customer> findAll();
	List<Customer> findByStatus(int status);
	List<Customer> findByEnabled(int status);
	List<Customer> findTop5ByOrderByIdDesc();
	List<Customer> findTop50ByOrderByIdDesc();
	List<Customer> findByEmailNotInAndEnabled(List<String> emails, int enabled);

	Customer findByIdAndStatus(int id, int status);
	Customer findByEmail(String email);
	Customer findByEmailAndEnabled(String email, int enabled);
	Customer findByPhone(String phone);
	Customer findByPhoneAndEnabled(String phone, int enabled);
	
	long countByCity(City city);
	long countByIsSeller(int isSeller);
	long countByIsSellerAndStatus(int isSeller, int status);
	long countByEnabled(int enabled);
	
	/*NATIVE QUERY*/
	@Query("SELECT new com.sumi.transaku.core.domains.Customer(c.id, c.name, c.email, c.password, c.gender, c.phone, c.isVerified, c.status, c.enabled, c.storeInventories) "
			+ "FROM Customer c WHERE c.id = ?1")
	Customer getCustomerProfile(int id);
	
}
