package com.sumi.transaku.core.services;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sumi.transaku.core.domains.IncomeStatement;
import com.sumi.transaku.core.domains.ResponseModel;
import com.sumi.transaku.core.domains.SellerCapital;
import com.sumi.transaku.core.repositories.CustomerRepository;
import com.sumi.transaku.core.repositories.SellerCapitalRepository;
import com.sumi.transaku.core.utils.StaticFields;
import com.sumi.transaku.core.utils.Utils;


@Service
public class SellerCapitalService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SellerCapitalService.class);
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	SellerCapitalRepository capitalRepository;
	
	
	@Autowired
	Utils utils;
	
	public ResponseModel initCapital(SellerCapital capital) {
		LOGGER.info("init SellerCapital");
		Date now = new Date();
		ResponseModel model = null;
		capital.setNote("INITIAL BALANCE CAPITAL");
		capital.setTrxType(StaticFields.TRX_TYPE_CREDIT);
		capital.setTrxDate(now);
		capital.setBalance(capital.getMutation());
		capital.setSeller(customerRepository.findOne(capital.getSeller().getId()));
		
		SellerCapital capitalSaved = capitalRepository.save(capital);
		//System.out.println(capitalSaved);        
		
		if(capitalSaved != null){
			//LOGGER.info(capitalSaved.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", capitalSaved.getId());
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to add seller capital", null);
		}
		
		return model;
		
	}
	
	public ResponseModel updateCapital(SellerCapital capital) {
		LOGGER.info("update SellerCapital");
		Date now = new Date();
		ResponseModel model = null;
		//capital.setTrxType(StaticFields.TRX_TYPE_CREDIT);
		SellerCapital lastCapital = capitalRepository.findTopBySellerOrderByTrxDateDesc(customerRepository.findOne(capital.getSeller().getId()));
		
		if(capital.getMutation() > 0)
			capital.setTrxType(StaticFields.TRX_TYPE_CREDIT);
		else
			capital.setTrxType(StaticFields.TRX_TYPE_DEBIT);
		
		capital.setMutation(Math.abs(capital.getMutation()));
		capital.setTrxDate(now);
		capital.setBalance(lastCapital.getBalance() + capital.getMutation());
		
		SellerCapital capitalUpdated = capitalRepository.save(capital);
		System.out.println(capitalUpdated);        
		
		if(capitalUpdated != null){
			LOGGER.info(capitalUpdated.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", capitalUpdated.getId());
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to update seller capital", null);
		}
		
		return model;
		
	}
	
	public ResponseModel updateCapitalByInventoryReStock(SellerCapital capital) {
		LOGGER.info("updateCapitalByInventoryReStock");
		Date now = new Date();
		ResponseModel model = null;
		//capital.setTrxType(StaticFields.TRX_TYPE_CREDIT);
		System.out.println(capitalRepository);
		System.out.println(customerRepository);
		SellerCapital lastCapital = capitalRepository.findTopBySellerOrderByTrxDateDesc(customerRepository.findOne(capital.getSeller().getId()));
		
		if(lastCapital != null){
			capital.setBalance(lastCapital.getBalance() - capital.getMutation());
			capital.setTrxDate(now);;
			capital.setTrxType(StaticFields.TRX_TYPE_DEBIT);
			capital.setNote("pembelian item: " + capital.getNote());
		}else{
			capital.setBalance(0 - capital.getMutation());
			capital.setTrxDate(now);;
			capital.setTrxType(StaticFields.TRX_TYPE_DEBIT);
			capital.setNote("pembelian item: " + capital.getNote());
		}
		
		SellerCapital capitalUpdated = capitalRepository.save(capital);
		System.out.println(capitalUpdated);        
		
		if(capitalUpdated != null){
			LOGGER.info(capitalUpdated.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", capitalUpdated.getId());
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to update seller capital - restock", null);
		}
		
		return model;
		
	}
	
	public ResponseModel updateCapitalByInventorySell(SellerCapital capital) {
		LOGGER.info("updateCapitalByInventorySell");
		Date now = new Date();
		ResponseModel model = null;

		SellerCapital lastCapital = capitalRepository.findTopBySellerOrderByTrxDateDesc(customerRepository.findOne(capital.getSeller().getId()));
		
		capital.setBalance(lastCapital.getBalance() + capital.getMutation());
		capital.setTrxDate(now);;
		capital.setTrxType(StaticFields.TRX_TYPE_CREDIT);
		capital.setNote("penjualan : " + capital.getSell().getCode());

		SellerCapital capitalUpdated = capitalRepository.save(capital);
		System.out.println(capitalUpdated);        
		
		if(capitalUpdated != null){
			LOGGER.info(capitalUpdated.toString());
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", capitalUpdated.getId());
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to update seller capital - restock", null);
		}
		
		return model;

	}
	
	public ResponseModel getReportByTrxDateBetween(int sellerId, Date startDate, Date endDate) {
		LOGGER.info("getReportByTrxDateBetween {} {} - {}", sellerId, startDate, endDate);
		Date now = new Date();
		ResponseModel model = null;
		List<IncomeStatement> statements = new ArrayList<>();
//		List<SellerCapital> capitals = capitalRepository.findBySellerAndTrxDateBetween(customerRepository.findOne(sellerId), startDate, endDate);
		LOGGER.info(customerRepository.findOne(sellerId).getName());
		List<SellerCapital> capitals = capitalRepository.findBySellerAndTrxDateBetweenOrderByTrxTypeDescTrxDateAsc(customerRepository.findOne(sellerId), startDate, endDate);
		IncomeStatement statement = null;
		double totalCredit = 0;
		double totalDebit = 0;
		double balance = 0;
		if(capitals != null){
			//LOGGER.info(capitalSaved.toString());
			for (SellerCapital sellerCapital : capitals) {
				if(sellerCapital.getTrxType().equalsIgnoreCase("c")){
					statement = new IncomeStatement();
					statement.setTrxType("C");
					statement.setCredit(sellerCapital.getMutation());
					statement.setDebit(0);
					statement.setNote(sellerCapital.getNote());
					statement.setTrxDate(sellerCapital.getTrxDate());
					statements.add(statement);
					totalCredit += sellerCapital.getMutation();
					balance += sellerCapital.getMutation();
				}else{
					statement = new IncomeStatement();
					statement.setTrxType("D");
					statement.setDebit(sellerCapital.getMutation());
					statement.setCredit(0);
					statement.setNote(sellerCapital.getNote());
					statement.setTrxDate(sellerCapital.getTrxDate());
					statements.add(statement);
					totalDebit += sellerCapital.getMutation();
					balance -= sellerCapital.getMutation();
				}
			}
			NumberFormat nf = NumberFormat.getInstance();
			nf.setGroupingUsed(false);

			Map<String, String> addInfo = new HashMap<>();
			addInfo.put("totalCredit", nf.format(totalCredit));
			addInfo.put("totalDebit", nf.format(totalDebit));
			addInfo.put("labaRugi", nf.format(balance));
			
			model = new ResponseModel(HttpStatus.OK, true, StaticFields.RESPONSE_CODE_SUCCESS, "success", addInfo, statements);
		}else{
			model = new ResponseModel(HttpStatus.OK, false, StaticFields.RESPONSE_CODE_DATA_NOT_FOUND, "failed to add seller capital", null);
		}
		
		return model;
		
	}
	
	
}
