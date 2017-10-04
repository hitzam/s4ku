package com.sumi.transaku.core.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.sumi.transaku.core.domains.Customer;
import com.sumi.transaku.core.domains.Sell;
import com.sumi.transaku.core.domains.SellerCapital;

public interface SellerCapitalRepository extends CrudRepository<SellerCapital, Integer>{

	List<SellerCapital> findAll();
	List<SellerCapital> findBySeller(Customer customer);
	SellerCapital findTopBySellerOrderByTrxDateDesc(Customer customer);
	List<SellerCapital> findBySellerAndTrxDateBetween(Customer customer, Date startDate, Date endDate);
	List<SellerCapital> findBySellerAndTrxDateBetweenOrderByTrxTypeDescTrxDateAsc(Customer customer, Date startDate, Date endDate);

}
