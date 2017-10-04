package com.sumi.transaku.core.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.sumi.transaku.core.domains.Province;

//@Transactional
public interface ProvinceRepository extends CrudRepository<Province, Integer>{

	List<Province> findAll();
	Province findByName(String name);
	
}
