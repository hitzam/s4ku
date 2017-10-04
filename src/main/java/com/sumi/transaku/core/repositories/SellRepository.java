package com.sumi.transaku.core.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.sumi.transaku.core.domains.Customer;
import com.sumi.transaku.core.domains.Sell;

public interface SellRepository extends CrudRepository<Sell, Integer>{

	List<Sell> findAll();
	List<Sell> findBySeller(Customer customer);
	List<Sell> findByBuyer(Customer customer);
	Sell findByCode(String code);
	Sell findTopBySellerOrderByIdDesc(Customer seller);
	List<Sell> findBySellerAndCreatedDateBetween(Customer customer, Date startDate, Date endDate);


	long count();
}
