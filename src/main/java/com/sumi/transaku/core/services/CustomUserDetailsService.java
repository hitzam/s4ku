/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sumi.transaku.core.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Service;

import com.sumi.transaku.core.domains.Customer;
import com.sumi.transaku.core.domains.Role;
import com.sumi.transaku.core.repositories.CustomerRepository;
import com.sumi.transaku.core.utils.StaticFields;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);
	
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomUserDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    	Customer customerCheck,customerLogin;
    	if(email.contains("@")){
    		LOGGER.info("login with customer's email: "+email);
    		customerCheck = customerRepository.findByEmail(email);
    		if(customerCheck == null){
    			throw new OAuth2Exception(String.format("User tidak terdaftar", email));
    		}else if(customerCheck != null && customerCheck.getStatus() == StaticFields.STATUS_CUSTOMER_REGISTERED){
    			throw new OAuth2Exception(String.format("User belum melakukan verifikasi"));
    		}else{	
    			customerLogin = customerRepository.findByEmailAndEnabled(email, StaticFields.STATUS_ENABLED);
    			if (customerLogin == null) {
    				throw new OAuth2Exception(String.format("User atau password salah!", email));
    			}
    			/*else if(customerLogin.getStatus() == StaticFields.STATUS_CUSTOMER_REGISTERED) {
    				throw new OAuth2Exception(String.format("User belum melakukan verifikasi"));
    			}*/
    		}
    	}else{
    		LOGGER.info("login with customer's phone number: "+email);
    		customerCheck = customerRepository.findByPhone(email);
    		if(customerCheck == null){
    			throw new OAuth2Exception(String.format("User tidak terdaftar", email));
    		}else if(customerCheck != null && customerCheck.getStatus() == StaticFields.STATUS_CUSTOMER_REGISTERED){
    			throw new OAuth2Exception(String.format("User belum melakukan verifikasi"));
    		}else{	
    			customerLogin = customerRepository.findByPhoneAndEnabled(email, StaticFields.STATUS_ENABLED);
    			if (customerLogin == null) {
    				throw new OAuth2Exception(String.format("User atau password salah!", email));
    			}
    			/*else if(customerLogin.getStatus() == StaticFields.STATUS_CUSTOMER_REGISTERED) {
    				throw new OAuth2Exception(String.format("User belum melakukan verifikasi"));
    			}*/
    		}
    	}
        LOGGER.info(customerLogin.toString());
        return new UserRepositoryUserDetails(customerLogin);
    }

    private final static class UserRepositoryUserDetails extends Customer implements UserDetails {

        private static final long serialVersionUID = 1L;

        private UserRepositoryUserDetails(Customer customer) {
            super(customer);
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            List<Role> roles = new ArrayList<Role>();
            roles.clear();
            roles.add(getRole());
            
            return roles;
        }
        @Override
        public String getUsername() {
            return getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

    }

}
