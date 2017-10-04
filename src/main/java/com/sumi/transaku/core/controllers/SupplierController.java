package com.sumi.transaku.core.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
/*import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;*/
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sumi.transaku.core.configs.MailConfiguration;
import com.sumi.transaku.core.configs.WebSecurityConfiguration;
import com.sumi.transaku.core.domains.Supplier;
import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.services.SupplierService;
import com.sumi.transaku.core.services.MailService;
import com.sumi.transaku.core.services.SupplierInventoryService;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;

@Controller
@RequestMapping("/supplier")
public class SupplierController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SupplierController.class);
	
	@Autowired
	WebSecurityConfiguration security;

	@Autowired
	SupplierService supplierService;

	@Autowired
	SupplierInventoryService supplierInventoryService;

	@Autowired
	MailConfiguration mailConfig;

	@Autowired
	MailService mailService;

	@Autowired
	Utils utils;
	
	private final ResourceLoader resourceLoader;
	@Autowired
	public SupplierController(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseModel> add(@RequestBody Supplier supplier) {
		LOGGER.info("register: "+supplier.getName());
		ResponseEntity<ResponseModel> entity = null;
		String plainPassword = supplier.getPassword();
		
		supplier.setPassword(utils.passwordEncoder().encode(supplier.getPassword()));
		
		ResponseModel rm = supplierService.addSupplier(supplier);
		
		//send email notification
		if(rm!=null){
			//mail to supplier
			String namaSupplier = supplier.getName();
			String subject = StaticFields.EMAIL_CUSTOMER_REGISTER_SUBJECT;
			String recipient = supplier.getEmail();
			String content = "";
			content += StaticFields.SMS_CUSTOMER_REGISTER_CONTENT.replace("xxxx", supplier.getEmail());
			content = content.replace("****", plainPassword);
			/*try {
				mailService.sendMailWithInline(mailConfig.getFrom(), namaSupplier, subject, recipient, content);
			} catch (MessagingException e) {
				e.printStackTrace();
			}*/
			
			//mail to CS
			//mailService.sendEmail(mailConfig.getFrom(), supplierService.getOperatorEmails(), "New Supplier Registration", "\n\nNama Supplier: " + supplierSaved.getName());
			
		}
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<ResponseModel> update(@RequestBody Supplier supplier, Principal principal) {
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm = supplierService.updateSupplier(supplier);		
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<ResponseModel> delete(@RequestBody Supplier supplier) {
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rmSupplier = supplierService.getSupplier(supplier.getId());
		Supplier supplierToDelete = (Supplier)rmSupplier.getData();
		supplierToDelete.setStatus(StaticFields.STATUS_DISABLED);
		
		//ResponseModel rm = supplierService.updateSupplier(supplierToDelete);		
		ResponseModel rm = supplierService.deleteSupplier(supplierToDelete);		
		
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<ResponseModel> changePassword(@RequestBody Supplier supplier, Principal principal) {
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm = supplierService.changePassword(supplier);		
		entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseModel> resetPassword(@RequestBody Supplier supplier) {
		LOGGER.info("resetPassword: {}",supplier.getId());
		ResponseEntity<ResponseModel> entity = null;
		
		ResponseModel rm = supplierService.resetPassword(supplier);
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  getAllSuppliers() {
		ResponseEntity<ResponseModel> entity = null;
		LOGGER.info("getAllSuppliers invoked");
		
		ResponseModel rm = supplierService.getAllSuppliers();
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/active", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  getActiveSuppliers() {
		ResponseEntity<ResponseModel> entity = null;
		LOGGER.info("getActiveSuppliers invoked");
		
		ResponseModel rm = supplierService.getActiveSuppliers();
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/search/{name}", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  searchSuppliers(@PathVariable("name") String name) {
		ResponseEntity<ResponseModel> entity = null;
		LOGGER.info("searchSuppliers invoked");
		
		ResponseModel rm = supplierService.searchByName(name);
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/searchWithLocation/{name}/{latitude}/{longitude}", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  searchSuppliersWithLocation(@PathVariable("name") String name, @PathVariable("latitude") String latitude, @PathVariable("longitude") String longitude) {
		ResponseEntity<ResponseModel> entity = null;
		try {
			latitude = URLDecoder.decode(latitude, "UTF-8").replace(",", ".");
			longitude = URLDecoder.decode(longitude, "UTF-8").replace(",", ".");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage());
		}
		LOGGER.info("searchWithLocationSuppliers invoked. {}  lat/long {}/{}", name, latitude, longitude);
		
		ResponseModel rm = supplierService.searchByName(name, Double.valueOf(latitude), Double.valueOf(longitude));
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/search/inventory/{name}", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  searchSuppliersByInventoryName(@PathVariable("name") String name) {
		ResponseEntity<ResponseModel> entity = null;
		LOGGER.info("searchSuppliersByInventoryName invoked");
		
		ResponseModel rm = supplierInventoryService.getInventoryByNameNative(name);
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/searchWithLocation/inventory/{name}/{latitude}/{longitude}", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  searchSuppliersByInventoryNamesearchWithLocation(@PathVariable("name") String name, @PathVariable("latitude") String latitude, @PathVariable("longitude") String longitude) {
		ResponseEntity<ResponseModel> entity = null;
		LOGGER.info("searchWithLocationSuppliersByInventoryName invoked");
		try {
			latitude = URLDecoder.decode(latitude, "UTF-8").replace(",", ".");
			longitude = URLDecoder.decode(longitude, "UTF-8").replace(",", ".");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage());
		}
		ResponseModel rm = supplierInventoryService.getInventoryByNameNative(name, Double.valueOf(latitude), Double.valueOf(longitude));
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	
	@RequestMapping(value = "/supplierDetail", method = RequestMethod.POST)
	public ResponseEntity<ResponseModel>  getSuppliersByEmail(@RequestBody Supplier supplier) {
		LOGGER.info("supplierDetail invoked");
		
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm = supplierService.getSupplierByEmail(supplier.getEmail());
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
//	public String getSupplier(@PathVariable("id") int id) {
	public ResponseEntity<ResponseModel>  getSupplier(@PathVariable("id") int id) {
		
		ResponseEntity<ResponseModel> entity = null;
		HttpHeaders headers = new HttpHeaders();
		ResponseModel rm = supplierService.getSupplier(id);
		
		headers.add("message", rm.getMsg());
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	
	@RequestMapping(value = "/profile", method = RequestMethod.PUT)
	public ResponseEntity<ResponseModel>  updateSupplierProfile(@RequestBody Supplier supplier) {
		
		ResponseEntity<ResponseModel> entity = null;
		HttpHeaders headers = new HttpHeaders();
		ResponseModel rm = supplierService.updateSupplierProfile(supplier);
		
		headers.add("message", rm.getMsg());
		
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/file/view/{filename:.+}")
	@ResponseBody
	public ResponseEntity<?> viewFile(@PathVariable String filename) {
		LOGGER.info("filename: " + filename);
		String folderPath = StaticFields.PATH_SUPPLIER_PICTURE();
		
		/*if(filename.contains("idcard"))
			folderPath = StaticFields.PATH_ID_CARD();
		else if(filename.contains("picture_"))
			folderPath = StaticFields.PATH_CUSTOMER_PICTURE();*/
		
		ResponseEntity<Object> entity = null;
		HttpHeaders headers = new HttpHeaders();
		// headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
		if(filename.endsWith(".jpg")){
			headers.add(HttpHeaders.CONTENT_TYPE, "image/jpg");
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename);
		}else if(filename.endsWith(".png")){
			headers.add(HttpHeaders.CONTENT_TYPE, "image/png");
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename);
		}
		try {
			
			entity = new ResponseEntity<Object>(resourceLoader.getResource("file:" + Paths.get(folderPath, filename).toString()), headers, HttpStatus.OK);
			
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.notFound().build();
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/file/download/{filename:.+}")
	@ResponseBody
	public ResponseEntity<?> getFile(@PathVariable String filename) {
		LOGGER.info("filename: " + filename);
		String folderPath = null;
		if(filename.contains("idcard"))
			folderPath = StaticFields.PATH_ID_CARD();
		else if(filename.contains("picture_"))
			folderPath = StaticFields.PATH_CUSTOMER_PICTURE();
		
		ResponseEntity<Object> entity = null;
		HttpHeaders headers = new HttpHeaders();
		// headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
		if(filename.endsWith(".doc")){
			headers.add(HttpHeaders.CONTENT_TYPE, "application/msword");
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
		}else if(filename.endsWith(".docx")){
			headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
		}else if(filename.endsWith(".pdf")){
			headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
		}else if(filename.endsWith(".jpg")){
			headers.add(HttpHeaders.CONTENT_TYPE, "image/jpg");
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
		}else if(filename.endsWith(".png")){
			headers.add(HttpHeaders.CONTENT_TYPE, "image/png");
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
		}
		try {
			
			entity = new ResponseEntity<Object>(resourceLoader.getResource("file:" + Paths.get(folderPath, filename).toString()), headers, HttpStatus.OK);
			
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.notFound().build();
		}
	}
}
