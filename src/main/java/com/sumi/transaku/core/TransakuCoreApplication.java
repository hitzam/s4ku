package com.sumi.transaku.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;

//@PropertySources({
//	@PropertySource(value = "file:application.properties", ignoreResourceNotFound = false)
//})
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class TransakuCoreApplication   extends SpringBootServletInitializer {
	
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TransakuCoreApplication.class);
    }

	public static void main(String[] args) {
		prepareSystem();
		SpringApplication.run(TransakuCoreApplication.class, args);
		//preConfigure();
	}
	
	public static void prepareSystem(){
		StaticFields.SERVER_OS = detectOS();
		Utils.createSystemDirectory();
	}
	
	public static int detectOS(){
		String os = System.getProperty("os.name");
		System.out.println("running on Operating System "+os);
		if(os.toLowerCase().contains("windows"))
			return 2;
		else
			return 1;
	}
	/*public static void preConfigure(){
		Utils utils = new Utils();
		utils.preConfigure();
	}*/
	
}
