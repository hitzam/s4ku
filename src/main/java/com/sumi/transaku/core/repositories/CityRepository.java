package com.sumi.transaku.core.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.sumi.transaku.core.domains.City;
import com.sumi.transaku.core.domains.Province;

//@Transactional
public interface CityRepository extends CrudRepository<City, Integer>{

	List<City> findAll();
	City findByName(String name);
	List<City> findByProvince(Province province);
	
}
