package com.sumi.transaku.core.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.websocket.server.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
/*import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;*/
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sumi.transaku.core.domains.BusinessType;
import com.sumi.transaku.core.domains.City;
import com.sumi.transaku.core.domains.ComboParam;
import com.sumi.transaku.core.domains.Customer;
import com.sumi.transaku.core.domains.InventoryCategory;
import com.sumi.transaku.core.domains.Province;
import com.sumi.transaku.core.domains.QtyUnit;
import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.domains.SupplierPurchaseOrder;
import com.sumi.transaku.core.domains.SupplierPurchaseOrderDetail;
import com.sumi.transaku.core.repositories.CustomerRepository;
import com.sumi.transaku.core.repositories.SellRepository;
import com.sumi.transaku.core.repositories.SupplierPurchaseOrderDetailRepository;
import com.sumi.transaku.core.repositories.SupplierPurchaseOrderRepository;
import com.sumi.transaku.core.repositories.SupplierRepository;
import com.sumi.transaku.core.services.BusinessTypeService;
import com.sumi.transaku.core.services.CategoryService;
import com.sumi.transaku.core.services.CityService;
import com.sumi.transaku.core.services.ProvinceService;
import com.sumi.transaku.core.services.QtyUnitService;
import com.sumi.transaku.core.services.SellService;
import com.sumi.transaku.core.services.SellerCapitalService;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;

@Controller
@RequestMapping("/apps")
public class AppsController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppsController.class);
	
	@Autowired
	ProvinceService provinceService;
	@Autowired
	SellService sellService;
	@Autowired
	SellerCapitalService capitalService;
	@Autowired
	CityService cityService;
	@Autowired
	BusinessTypeService businessTypeService;
	@Autowired
	CategoryService categoryService;
	@Autowired
	QtyUnitService qtyUnitService;
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	SellRepository sellRepository;
	@Autowired
	SupplierRepository supplierRepository;
	@Autowired
	SupplierPurchaseOrderRepository supplierPoRepository;
	@Autowired
	SupplierPurchaseOrderDetailRepository supplierPoDetailRepository;

	@Autowired
	Utils utils;
	
	

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/populateCombo", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  populateCombo() {
		LOGGER.info("populateCombo invoked");
		
		ResponseEntity<ResponseModel> entity = null;
		
		ComboParam comboParam = new ComboParam();
		
		comboParam.setProvinces((List<Province>)provinceService.getAllProvinces().getData());
		comboParam.setCities((List<City>)cityService.getAllCities().getData());
		comboParam.setBusinessTypes((List<BusinessType>)businessTypeService.getAllBusinessTypes().getData());
		comboParam.setInventoryCategories((List<InventoryCategory>)categoryService.getAllCategories().getData());
		comboParam.setQtyUnits((List<QtyUnit>)qtyUnitService.getAllUnits().getData());
		
		ResponseModel rm =  new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", comboParam);
		
		//entity = new ResponseEntity<Map<String, Object>>(resp, headers, rm.getStatus());
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/generalInfo", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  generalInfo() {
		LOGGER.info("generalInfo invoked");
		
		ResponseEntity<ResponseModel> entity = null;
		List<Customer> lastRegSeller = customerRepository.findTop50ByOrderByIdDesc();
		List<Map<String, Object>> mapList = new ArrayList<>();
		for (Customer customer : lastRegSeller) {
			Map<String, Object> map = new HashMap<>();
			map.put("date", customer.getCreatedDate()==null?null:utils.sdtf.format(customer.getCreatedDate()));
			map.put("name", customer.getName());
			map.put("phone", customer.getPhone());
			map.put("email", customer.getEmail());
			map.put("status", customer.getStatus()==1?"unverified":"verified");
			mapList.add(map);
		}
		
		Map<String, Object> generalInfo = new HashMap<String, Object>();
		//generalInfo.put("sellerCount", customerRepository.countByIsSeller(1));
		generalInfo.put("sellerCount", customerRepository.countByIsSellerAndStatus(1,StaticFields.STATUS_CUSTOMER_VERIFIED));
		generalInfo.put("sellCount", sellRepository.count());
		generalInfo.put("supplierCount", supplierRepository.count());
		generalInfo.put("lastRegisteredSeller", mapList);
		
		
		ResponseModel rm =  new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", generalInfo);
		
		//entity = new ResponseEntity<Map<String, Object>>(resp, headers, rm.getStatus());
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/tmpPoList2", method = RequestMethod.GET)
	public ResponseEntity<Object>  tmpPoList2() {
		LOGGER.info("tmpPoList invoked");
		
		ResponseEntity<Object> entity = null;
		List<SupplierPurchaseOrder> poList = supplierPoRepository.findByStatus(StaticFields.STATUS_PURCHASE_ORDER_REGISTERED);
		List<Map<String, Object>> mapList = new ArrayList<>();
		String[][] json= new String[poList.size()][9];
		int idx = 0;
		for (SupplierPurchaseOrder po : poList) {
			Map<String, Object> map = new HashMap<>();
			List<Object> detailList = new ArrayList<>();
			json[idx][0] = po.getCreatedDate()==null?null:utils.sdtf.format(po.getCreatedDate());
			json[idx][1] = po.getCustomer().getName();
			json[idx][2] = po.getCustomer().getPhone();
			json[idx][3] = po.getCustomer().getAddress();
			json[idx][4] = po.getSupplier().getName();
			json[idx][5] = po.getSupplier().getPhone();
			json[idx][6] = po.getSupplier().getAddress();
			json[idx][7] = String.valueOf(po.getTotalPrice());
			json[idx][8] = po.getCode();
			
			idx++;
		}
		
		Map<String, Object> poInfo = new HashMap<String, Object>();
		poInfo.put("data", json);
		
		
		ResponseModel rm =  new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", poInfo);
		
		//entity = new ResponseEntity<Map<String, Object>>(resp, headers, rm.getStatus());
		entity = new ResponseEntity<Object>(poInfo, rm.getStatus());
		
		return entity;
	}

	@RequestMapping(value = "/tmpPoList2Detail/{code}", method = RequestMethod.GET)
	public ResponseEntity<Object>  tmpPoList2Detail(@PathVariable("code")String code) {
		LOGGER.info("tmpPoList invoked");
		
		ResponseEntity<Object> entity = null;
		List<SupplierPurchaseOrderDetail> listDetail = supplierPoDetailRepository.findBySupplierPurchaseOrder(supplierPoRepository.findByCode(code));
		String[][] json= new String[listDetail.size()][4];
		int idx = 0;
		for (SupplierPurchaseOrderDetail det : listDetail) {
			json[idx][0] = det.getInventory().getName();
			json[idx][1] = String.valueOf(det.getQty());
			json[idx][2] = String.valueOf(det.getPrice());
			json[idx][3] = String.valueOf(det.getSubTotal());
			
			idx++;
		}
		
		Map<String, Object> poInfo = new HashMap<String, Object>();
		poInfo.put("data", json);
		
		
		ResponseModel rm =  new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", poInfo);
		
		//entity = new ResponseEntity<Map<String, Object>>(resp, headers, rm.getStatus());
		entity = new ResponseEntity<Object>(poInfo, rm.getStatus());
		
		return entity;
	}
	
	@RequestMapping(value = "/tmpPoList", method = RequestMethod.GET)
	public ResponseEntity<ResponseModel>  tmpPoList() {
		LOGGER.info("tmpPoList invoked");
		
		ResponseEntity<ResponseModel> entity = null;
		List<SupplierPurchaseOrder> poList = supplierPoRepository.findByStatus(StaticFields.STATUS_PURCHASE_ORDER_REGISTERED);
		List<Map<String, Object>> mapList = new ArrayList<>();
		for (SupplierPurchaseOrder po : poList) {
			Map<String, Object> map = new HashMap<>();
			List<Object> detailList = new ArrayList<>();
			map.put("date", po.getCreatedDate()==null?null:utils.sdtf.format(po.getCreatedDate()));
			map.put("sellerName", po.getCustomer().getName());
			map.put("sellerPhone", po.getCustomer().getPhone());
			map.put("sellerAddress", po.getCustomer().getAddress());
			map.put("supplierName", po.getSupplier().getName());
			map.put("supplierPhone", po.getSupplier().getPhone());
			map.put("supplierAddress", po.getSupplier().getAddress());
			
			if(!po.getOrderDetails().isEmpty()){
				Map<String, Object> det = new HashMap<>();
				for (SupplierPurchaseOrderDetail detail : po.getOrderDetails()) {
					det.put("item", detail.getInventory().getName());
					det.put("price", detail.getInventory().getPrice());
					det.put("qty", detail.getInventory().getQty());
					detailList.add(det);
				}
				map.put("itemOrdered", detailList);
			}
			map.put("totalPrice", po.getTotalPrice());
			mapList.add(map);
		}
			
		Map<String, Object> poInfo = new HashMap<String, Object>();
		poInfo.put("purchaseOrderList", mapList);
		
		
		ResponseModel rm =  new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", poInfo);
		
		//entity = new ResponseEntity<Map<String, Object>>(resp, headers, rm.getStatus());
		entity = new ResponseEntity<ResponseModel>(rm, rm.getStatus());
		
		return entity;
	}
	
	//@RequestMapping(value = "/webReport/{sig}/{startDate}/{endDate}", method = RequestMethod.GET)
	@RequestMapping(value = "/webReport", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<ResponseModel> webReport(@RequestParam("sig")String sig, @RequestParam("startDate")String startDate, @RequestParam("endDate")String endDate) {
		LOGGER.info("item Sold Summary invoked by: {} {} - {}", sig, startDate, endDate);
		ResponseEntity<ResponseModel> entity = null;
		ResponseModel rm, rmItemSold, rmTrxHistory, rmLabaRugi;
		Map<String, Object> mapData = new HashMap<>();
		
		rm = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", null);
		//rm = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to retrieve data", null);
		
		//sig --> encrypted string of [customerId]|[date(yyyy-MM-dd HH)]|[email]
			try {
				String decodedSig = URLDecoder.decode(sig, "UTF-8");
				//String decryptedSig = utils.decrypt(decodedSig, StaticFields.REPORT_ENCRYPTION_SECRET_KEY);
				String decryptedSig = utils.decrypt(sig, StaticFields.REPORT_ENCRYPTION_SECRET_KEY);
				String[] decryptedArraySig = decryptedSig.split("\\|");
				Customer customer = customerRepository.findOne(Integer.valueOf(decryptedArraySig[0]));
				LOGGER.info("decryptedArraySig[1] {}",decryptedArraySig[1]);
				LOGGER.info("decryptedArraySig[2] {}",decryptedArraySig[2]);
				if(utils.sdhf.format(new Date()).equalsIgnoreCase(decryptedArraySig[1]) && customer.getEmail().equalsIgnoreCase(decryptedArraySig[2])){
					//Item Sold Report
					rmItemSold = sellService.getItemSoldByCustomerAndDateBetween(Integer.valueOf(decryptedArraySig[0]), utils.sdhmf.parse(startDate), utils.sdhmf.parse(endDate));
					
					//Trx History Report
					rmTrxHistory = sellService.getSellByCustomerAndDateBetween(Integer.valueOf(decryptedArraySig[0]), utils.sdhmf.parse(startDate), utils.sdhmf.parse(endDate));
					
					//Laba Rugi Report
					rmLabaRugi = capitalService.getReportByTrxDateBetween(Integer.parseInt(decryptedArraySig[0]), utils.sdtf.parse(startDate+":01"), utils.sdtf.parse(endDate+":01"));
					
					mapData.put("itemSold", rmItemSold.getData());
					mapData.put("trxHistory", rmTrxHistory.getData());
					mapData.put("labaRugi", rmLabaRugi.getData());
					
					rm.setData(mapData);
					
					entity = new ResponseEntity<ResponseModel>(rm, HttpStatus.OK);
				}else{
					
				}
			} catch (ParseException e) {
				rmItemSold = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_FAILED, "failed to retrieve data", null);
				entity = new ResponseEntity<ResponseModel>(rmItemSold, HttpStatus.OK);
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}		
			
		
		return entity;
	}
		
}
