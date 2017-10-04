package com.sumi.transaku.core.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.sumi.transaku.core.domains.Role;

public interface RoleRepository extends CrudRepository<Role, Integer>{

	List<Role> findByNameContainingIgnoreCase(String name);
	List<Role> findAll();
	List<Role> findByNameNot(String name);
	
}
