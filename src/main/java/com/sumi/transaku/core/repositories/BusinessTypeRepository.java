package com.sumi.transaku.core.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.sumi.transaku.core.domains.BusinessType;

//@Transactional
public interface BusinessTypeRepository extends CrudRepository<BusinessType, Integer>{

	List<BusinessType> findAll();
	BusinessType findByName(String name);
	
}
