package com.sumi.transaku.core.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.sumi.transaku.core.domains.QtyUnit;

//@Transactional
public interface QtyUnitRepository extends CrudRepository<QtyUnit, Integer>{

	List<QtyUnit> findAll();
	QtyUnit findByName(String name);
	
}
