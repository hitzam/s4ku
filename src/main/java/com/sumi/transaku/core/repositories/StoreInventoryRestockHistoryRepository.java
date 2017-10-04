package com.sumi.transaku.core.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.sumi.transaku.core.domains.StoreInventoryRestockHistory;

public interface StoreInventoryRestockHistoryRepository extends CrudRepository<StoreInventoryRestockHistory, Integer>{

	List<StoreInventoryRestockHistory> findAll();
	List<StoreInventoryRestockHistory> findBySellerId(int sellerId);
	List<StoreInventoryRestockHistory> findBySellerIdAndCreatedDateBetween(int sellerId, Date startDate, Date endDate);
	StoreInventoryRestockHistory findByItemCode(String itemCode);
	StoreInventoryRestockHistory findTopBySellerIdOrderByCreatedDateDesc(int sellerId);
	StoreInventoryRestockHistory findTopBySellerIdOrderByCreatedDate(int sellerId);
	
}
