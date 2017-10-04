/*
 * Copyright 2014 the original author or authors.
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
package com.sumi.transaku.core.configs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import com.sumi.transaku.core.domains.Customer;
import com.sumi.transaku.core.services.CustomUserDetailsService;
import com.sumi.transaku.core.services.CustomerService;
import com.sumi.transaku.core.utils.Utils;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private CustomerService customerService;
    
	@Autowired
	private Utils utils;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	auth.eraseCredentials(false).
    	userDetailsService(userDetailsService).passwordEncoder(utils.passwordEncoder());
//        auth.userDetailsService(userDetailsService);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private static final String RESOURCE_ID = "transaku_resource";

    @Configuration
    @EnableResourceServer
    @Order(-1)
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            // @formatter:off
            resources.resourceId(RESOURCE_ID);
            // @formatter:on
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.httpBasic().disable()
                    .authorizeRequests()
                    .antMatchers("/signUp").permitAll()
                    .antMatchers("/customer/**").authenticated()
                    .antMatchers("/inventory/**").authenticated()
                    //.antMatchers("/apps/**").authenticated()
                    .antMatchers("/sell/**").authenticated()
                    .antMatchers("/supplier/**").authenticated()
                    //.antMatchers("/transaku/replacement/**").authenticated()
                    .antMatchers(HttpMethod.OPTIONS, "/oauth/token/**").permitAll()
                    ;
            
            /*http
            // your custom configuration goes here
            .exceptionHandling()
            .authenticationEntryPoint((request, response, e) -> {
            	//String json = String.format("{\"message\": \"%s\"}", e.getMessage());
                String json = String.format("{\"message\": \"%s\"}", "salah cuy!"+authentication.getPrincipal());
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(json);                
            });*/
            
            /*
            http
                .authorizeRequests()
                .anyRequest().authenticated()
                .antMatchers(HttpMethod.OPTIONS, "/oauth/token").permitAll();
             */
        }
        
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {


        @Autowired
        //@Qualifier("authenticationManager")
        protected AuthenticationManager authenticationManager;

        @Autowired
        private CustomUserDetailsService userDetailsService;
        
        @Autowired
        private DataSource dataSource;

        //private TokenStore tokenStore = new InMemoryTokenStore();
        //private TokenStore tokenStore = new JdbcTokenStore(dataSource);
        @Bean
        public TokenStore tokenStore() {
            return new JdbcTokenStore(dataSource);
        }
        
        
        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            // @formatter:off
            endpoints
            		//.tokenStore(this.tokenStore)
                    .tokenStore(tokenStore())
                    .authenticationManager(this.authenticationManager)
                    .tokenEnhancer(tokenEnhancer())
                    .userDetailsService(userDetailsService)
                    .exceptionTranslator(e -> {
                    	
                    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                        if (e instanceof OAuth2Exception) {
                            OAuth2Exception oAuth2Exception = (OAuth2Exception) e;

                            return ResponseEntity
                                    .status(oAuth2Exception.getHttpErrorCode())
                                    .body(new OAuth2Exception("User atau password salah!"));
                        } else {
                            throw e;
                        }
                    })
                    ;
            // @formatter:on
        }
        
        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception
        {
           oauthServer.checkTokenAccess("isAuthenticated()");    
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            // @formatter:off
            /*clients
                    .inMemory()
                    .withClient("transaku")
                    .authorizedGrantTypes("password", "refresh_token")
                    .authorities("ROLE_CUSTOMER", "ROLE_ADMIN")
                    .scopes("read", "write")
                    .resourceIds(RESOURCE_ID)
                    .secret("tR4n5aKuC0r3")
                    .accessTokenValiditySeconds(10800);*/
        	
        	clients.jdbc(dataSource);
            // @formatter:on
        }
        

        @Bean
        @Primary
        public DefaultTokenServices tokenServices() {
            DefaultTokenServices tokenServices = new DefaultTokenServices();
            tokenServices.setSupportRefreshToken(true);
            //tokenServices.setTokenStore(this.tokenStore);
            tokenServices.setTokenStore(tokenStore());
            tokenServices.setTokenEnhancer(tokenEnhancer());
            return tokenServices;
        }
        
        @Bean
        public TokenEnhancer tokenEnhancer() {
            return new CustomTokenEnhancer();
        }
    }
    
    public Customer getProfile(){
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	String currentPrincipalName = authentication.getName();
    	System.out.println("currentPrincipalName: "+currentPrincipalName);
    	return (Customer)customerService.getCustomerByEmail(currentPrincipalName).getData();
    }
}
