package com.sumi.transaku.core.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.sumi.transaku.core.domains.ApplicationProperties;

public interface ApplicationPropertiesRepository extends CrudRepository<ApplicationProperties, Integer>{

	List<ApplicationProperties> findAll();
	ApplicationProperties findByName(String name);
	List<ApplicationProperties> findByNameStartingWith(String name);
	List<ApplicationProperties> findByNameContaining(String name);
	
}
