package com.sumi.transaku.core.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.sumi.transaku.core.domains.Sell;
import com.sumi.transaku.core.domains.SellDetail;

public interface SellDetailRepository extends CrudRepository<SellDetail, Integer>{

	List<SellDetail> findAll();
	List<SellDetail> findBySell(Sell sell);

}
