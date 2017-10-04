package com.sumi.transaku.core.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sumi.transaku.core.domains.Customer;
import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.domains.StoreInventory;
import com.sumi.transaku.core.domains.Supplier;
import com.sumi.transaku.core.domains.SupplierInventory;
import com.sumi.transaku.core.repositories.CustomerRepository;
import com.sumi.transaku.core.repositories.RoleRepository;
import com.sumi.transaku.core.repositories.StoreInventoryRepository;
import com.sumi.transaku.core.repositories.SupplierInventoryRepository;
import com.sumi.transaku.core.repositories.SupplierRepository;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;


@Service
public class SupplierService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SupplierService.class);
	
	@Autowired
	SupplierRepository supplierRepository;

	@Autowired
	SupplierInventoryRepository supplierInventoryRepository;
	

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	StoreInventoryRepository storeInventoryRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	SmsService smsService;
	
	@Autowired
	Utils utils;

	
	public ResponseModel addSupplier(Supplier supplier) {
		LOGGER.info("add supplier");
		ResponseModel model = null;
		//String pathSuratPendirian = StaticFields.PATH_SURAT_PENDIRIAN();
		String pathPicture = StaticFields.PATH_SUPPLIER_PICTURE();
		String verificationCode = null;
		try {

			if(supplier.getPictureBase64()!=null && supplier.getPictureBase64().length()>10){
				//String decodedPicture = URLDecoder.decode(supplier.getKartuIdentitasBase64(), "UTF-8");
				String decodedPicture = supplier.getPictureBase64();
				supplier.setPicturePath(Utils.base64ToFile(decodedPicture, pathPicture, "picture_", supplier.getEmail()));
			}
			
			//Set trial period
			supplier.setStatus(StaticFields.STATUS_ENABLED);
			supplier.setCreatedDate(new Date());
			
			Supplier supplierSaved = supplierRepository.save(supplier);
			System.out.println(supplierSaved);        
			
			if(supplierSaved != null){
				LOGGER.info(supplierSaved.toString());
				
				//send confirmation via sms
				smsService.sendSms(supplier.getPhone(), StaticFields.SMS_CUSTOMER_REGISTER_BUYER_CONTENT.replace("xxxxxx", verificationCode));
				
				model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", supplierSaved.getId());
			}else{
				model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_FAILED, "failed to add supplier", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return model;
	}
	
	public ResponseModel updateSupplier(Supplier supplier) {
		String pathPicture = StaticFields.PATH_ID_CARD();
		
		ResponseModel model = null;
		
		Supplier existingSupp = supplierRepository.findOne(supplier.getId());
		
		if(supplier.getPassword()!=null && !supplier.getPassword().equalsIgnoreCase("")){
			supplier.setPassword(utils.passwordEncoder().encode(supplier.getPassword()));
		}else{
			supplier.setPassword(existingSupp.getPassword());
		}
		
		if(supplier.getPictureBase64()!=null && supplier.getPictureBase64().length()>10){
			//String decodedPicture = URLDecoder.decode(supplier.getKartuIdentitasBase64(), "UTF-8");
			String decodedPicture = supplier.getPictureBase64();
			supplier.setPicturePath(Utils.base64ToFile(decodedPicture, pathPicture, "picture_", supplier.getEmail()));
		}else{
			supplier.setPicturePath(existingSupp.getPicturePath());
		}
		
		Supplier supplierUpdated = supplierRepository.save(supplier);
		System.out.println(supplierUpdated);        
		
		if(supplierUpdated != null){
			LOGGER.info(supplierUpdated.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", supplierUpdated);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to update supplier", null);
		}
		
		return model;
	}
	
	public ResponseModel updateSupplierPicturePath(String email, String path) {
		
		ResponseModel model = null;
		
		Supplier existingSupp = supplierRepository.findByEmail(email);
		existingSupp.setPicturePath(path);
		
		Supplier supplierUpdated = supplierRepository.save(existingSupp);
		System.out.println(supplierUpdated);        
		
		if(supplierUpdated != null){
			LOGGER.info(supplierUpdated.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", supplierUpdated);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to update supplier", null);
		}
		
		return model;
	}
	
	public ResponseModel updateSupplierProfile(Supplier supplier) {
		ResponseModel model = null;
		
		Supplier existingCust = supplierRepository.findOne(supplier.getId());
		
		if(existingCust!=null){
			existingCust.setName(supplier.getName());
			existingCust.setEmail(supplier.getEmail());
			existingCust.setPhone(supplier.getPhone());
		}
		
		Supplier supplierUpdated = supplierRepository.save(supplier);
		System.out.println(supplierUpdated);        
		
		if(supplierUpdated != null){
			LOGGER.info(supplierUpdated.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", supplierUpdated);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to update supplier", null);
		}
		
		return model;
	}
	
	public ResponseModel changePassword(Supplier supplier) {
		
		ResponseModel model = null;
		
		Supplier existingCust = supplierRepository.findOne(supplier.getId());
		
		if(supplier.getPassword()!=null && !supplier.getPassword().equalsIgnoreCase("")){
			if(utils.passwordEncoder().matches(supplier.getOldPassword(), existingCust.getPassword())){
				existingCust.setPassword(utils.passwordEncoder().encode(supplier.getPassword()));
				supplierRepository.save(existingCust);
				model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", null);
			}else{
				model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_SUCCESS, "old password do not match", null);
			}
		}
				
		return model;
	}

	public ResponseModel resetPassword(Supplier supplier) {

		ResponseModel model = null;

		Supplier existingSupplier = supplierRepository.findOne(supplier.getId());

		if (supplier.getEmail() != null && !supplier.getEmail().equalsIgnoreCase("")) {
			if (supplier.getEmail().equalsIgnoreCase(existingSupplier.getEmail())) {
				String newPassword = utils.generatePassword();
				existingSupplier.setPassword(utils.passwordEncoder().encode(newPassword));
				supplierRepository.save(existingSupplier);

				//send confirmation via sms
				try {
					smsService.sendSms(existingSupplier.getPhone(), StaticFields.SMS_CUSTOMER_RESET_PASSWORD.replace("******", newPassword));
				} catch (Exception e) {
					LOGGER.error(e.getMessage());
					e.printStackTrace();
				}

				model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", null);
			} else {
				model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_SUCCESS,
						"supplier's email do not match", null);
			}
		}

		return model;
	}

	public ResponseModel deleteSupplier(Supplier supplier) {
		LOGGER.info("delete supplier");
		
		ResponseModel model = null;
		supplierRepository.delete(supplier);
		
		model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", null);
		
		return model;
	}
	
	
	public ResponseModel getAllSuppliers(){
		LOGGER.info("get All Supplier");
		ResponseModel model = null;
		List<Supplier> suppliers = supplierRepository.findAll();
		
		if(suppliers != null){
			suppliers.forEach(supplier->{
				supplier.setPassword(null);
			});
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", suppliers);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getActiveSuppliers(){
		LOGGER.info("get active Supplier");
		ResponseModel model = null;
		
		//List<Supplier> suppliers = supplierRepository.findByStatus(StaticFields.STATUS_ENABLED);
		//:TODO temporary test to fetch supplier data that is copied from seller (status=6)
		List<Supplier> suppliers = supplierRepository.findByStatusGreaterThan(StaticFields.STATUS_DISABLED);
		
		if(suppliers != null){
			suppliers.forEach(supplier->{
				supplier.setPassword(null);
			});
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", suppliers);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getSupplier(int supplierId){
		LOGGER.info("get supplier");
		ResponseModel model = null;
		Supplier supplier = supplierRepository.findOne(supplierId);
		System.out.println(supplier);        
		
		if(supplier != null){
			LOGGER.info(supplier.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", supplier);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	
	public ResponseModel getSupplierByEmail(String email){
		LOGGER.info("get supplier "+email);
		ResponseModel model = null;
//		Supplier supplier = supplierRepository.findByEmailContaining(email);
		Supplier supplier = supplierRepository.findByEmail(email);
		//clear password
		System.out.println(supplier);        
		
		if(supplier != null){
			LOGGER.info(supplier.toString());
			supplier.setPassword(null);
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", supplier);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel getSupplierByEmailPasswordIncluded(String email){
		LOGGER.info("get supplier "+email);
		ResponseModel model = null;
//		Supplier supplier = supplierRepository.findByEmailContaining(email);
		Supplier supplier = supplierRepository.findByEmail(email);
		//clear password
		System.out.println(supplier);        
		
		if(supplier != null){
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", supplier);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel searchByName(String name){
		LOGGER.info("search supplier "+name);
		ResponseModel model = null;
		List<Supplier> suppliers = supplierRepository.findByNameContaining(name);
		//clear password
		System.out.println(suppliers);        
		
		if(suppliers != null){
			suppliers.forEach(supplier->{
				supplier.setSupplierInventories(null);
				supplier.setPassword(null);
			});
			
			//suppliers = sortSupplierByNearestLocation(suppliers, -6.362110, 106.919685);
			
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", suppliers);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}
	
	public ResponseModel searchByName(String name, Double latitude, Double longitude){
		LOGGER.info("search supplier "+name);
		ResponseModel model = null;
		//List<Supplier> suppliers = supplierRepository.findByNameContaining(name);
		//:TODO temporary test to fetch supplier data that is copied from seller (status=6)
        List<Supplier> suppliers = supplierRepository.findByNameContainingAndStatusGreaterThan(name, StaticFields.STATUS_SUPPLIER_DISABLED);
        //clear password
        System.out.println(suppliers);        
        
        if(suppliers != null){
        	suppliers.forEach(supplier->{
        		supplier.setSupplierInventories(null);
				supplier.setPassword(null);
			});
        	
        	if( latitude != null && longitude != null){
        		suppliers = sortSupplierByNearestLocation(suppliers, latitude, longitude);
        	}
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", suppliers);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
        
        return model;
	}

	//-6.362110, 106.919685
	public List<Supplier> sortSupplierByNearestLocation(List<Supplier> listSuppliers, Double latitude, Double longitude){
		double ax = latitude;
		double ay = longitude;
		Double[] supplierDistances = new Double[listSuppliers.size()];
		Map<Double, Supplier> distanceSuppliers = new TreeMap<>();
		List<Supplier> suppliers = new ArrayList<>();
		List<Supplier> suppliersNoCoordinate = new ArrayList<>();
		//Map<Double, String> result = new HashMap<>();
		
		for (int i = 0; i<listSuppliers.size() ; i++) {
			Supplier supplier = listSuppliers.get(i);
			
			if(supplier.getLatitude() == null || supplier.getLongitude() == null){
				suppliersNoCoordinate.add(supplier);
				continue;
			}
			
			double bx = Double.valueOf(supplier.getLatitude());
			double by = Double.valueOf(supplier.getLongitude());

			double dx = ax - bx;
			double dy = ay - by;
			double distance = Math.sqrt(dx * dx + dy * dy);

			//supplier.setDistance(distance);
			supplier.setDistance(utils.measurePoints(latitude, longitude, Double.valueOf(supplier.getLatitude()), Double.valueOf(supplier.getLongitude())));
			
			supplierDistances[i] = distance;
			distanceSuppliers.put(distance, supplier);
		}
		
		for(Entry<Double, Supplier> e : distanceSuppliers.entrySet()){
			suppliers.add(e.getValue());
		}
		
		//add suppliers with no coordinate
		suppliers.addAll(suppliersNoCoordinate);
		
		return suppliers;
		/*Double smallest = Double.MAX_VALUE;

		for (int idx = 0; idx < supplierDistances.length; idx++) {
			if (smallest > supplierDistances[idx]) {
				smallest = supplierDistances[idx];
			}
		}

		System.out.println("nearest: " + smallest + " - " + distanceSuppliers.get(smallest));
		String siteName = distanceSuppliers.get(smallest).split("#")[0];
		String btsType = distanceSuppliers.get(smallest).split("#")[1];
		double lat = Double.valueOf(distanceSuppliers.get(smallest).split("#")[2].split(",")[0]);
		double lon = Double.valueOf(distanceSuppliers.get(smallest).split("#")[2].split(",")[1]);
		//result.put(smallest, distanceCoordinate.get(smallest));
		double dist = measurePoints(latitude, longitude, lat, lon);
		return new NearestBts(siteName, btsType, dist, lat, lon);*/
		
	}
		
/*	public ResponseModel getSupplierProfile(int supplierId){
		LOGGER.info("get supplier");
		ResponseModel model = null;
		Supplier supplier = supplierRepository.getSupplierProfile(supplierId);
		System.out.println(supplier);        
		
		if(supplier != null){
			LOGGER.info(supplier.toString());
			supplier.setPassword(null);
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", supplier);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		}
		
		return model;
	}*/
		
		public void convertSellerToSupplier(){
			LOGGER.info("convertSellerToSupplier invoked");
			
			Supplier supplier = null;
			List<SupplierInventory> supplierInventories= new ArrayList<>();
			SupplierInventory supplierInventory = null;
			List<String> existingSellerSuppliersEmail = new ArrayList<>();

			List<Supplier> existingSellerSuppliers = supplierRepository.findByStatus(StaticFields.STATUS_SUPPLIER_SELLER);
			
			for (Supplier existSupplier : existingSellerSuppliers) {
				existingSellerSuppliersEmail.add(existSupplier.getEmail());
			}
			
			List<Customer> unconvertedSellers = null;
			if(existingSellerSuppliersEmail.size() > 0)
				unconvertedSellers = customerRepository.findByEmailNotInAndEnabled(existingSellerSuppliersEmail, StaticFields.STATUS_ENABLED);
			else
				unconvertedSellers = customerRepository.findByEnabled(StaticFields.STATUS_ENABLED);
				
			LOGGER.info("number of unconvertedSellers will be converted: {}", unconvertedSellers.size());
			
			for (Customer c : unconvertedSellers) {
				supplier = new Supplier(c.getBusinessName(), c.getEmail(), c.getPassword(), null, c.getAddress(), c.getCity(), c.getPostalCode(), c.getPhone(), c.getLatitude(), c.getLongitude(), c.getCreatedDate(), c.getPicturePath(), StaticFields.STATUS_SUPPLIER_SELLER);
				Supplier savedSupplier = supplierRepository.save(supplier);
				
				supplierInventories = new ArrayList<>();
				for (StoreInventory storeInv : storeInventoryRepository.findBySeller(c)) {
					supplierInventory = new SupplierInventory(storeInv.getCode(), storeInv.getCategory(), savedSupplier, storeInv.getName(), storeInv.getDescription(), storeInv.getPrice(), storeInv.getQty(), storeInv.getQtyUnit(), storeInv.getWeight(), storeInv.getCreatedDate(), storeInv.getNote());
					supplierInventories.add(supplierInventory);
				}
				
				Set<SupplierInventory> supplierInventoriesSet= new HashSet<>(supplierInventories);
				supplier.setSupplierInventories(supplierInventoriesSet);
				supplierRepository.save(supplier);
			}
			
		}

}
