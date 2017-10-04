package com.sumi.transaku.core.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import com.sumi.transaku.core.domains.Role;
import com.sumi.transaku.core.domains.User;

//@Transactional
public interface UserRepository extends CrudRepository<User, Integer>{

//	List<User> findByNamaLengkapContainingIgnoreCase(String namaLengkap);
	List<User> findAll();
	List<User> findByRole(Role role);

	User findByemail(String email);
	
	long countByRole(Role roles);
	long countByRoleAndEnabled(Role role, int enabled);
}
