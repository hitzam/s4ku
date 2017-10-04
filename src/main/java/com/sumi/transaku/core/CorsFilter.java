package com.sumi.transaku.core;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class CorsFilter implements Filter {

  /*void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
    HttpServletResponse response = (HttpServletResponse) res;
    HttpServletRequest request = (HttpServletRequest) req;
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
    response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
    response.setHeader("Access-Control-Max-Age", "3600");
    if (request.getMethod()!="OPTIONS") {
      chain.doFilter(req, res);
    } else {
    }
  }*/
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

	    HttpServletRequest request = (HttpServletRequest) req;
	    HttpServletResponse response = (HttpServletResponse) res;

	    //response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
	    response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, X-Requested-With, remember-me");
	    response.setHeader("Access-Control-Allow-Credentials", "true");
	    response.setHeader("Access-Control-Max-Age", "3600");
	    if (request.getMethod()!="OPTIONS") {
	        chain.doFilter(req, res);
	      } else {
	      }

	    //chain.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig filterConfig) {}

	public void destroy() {}

}
