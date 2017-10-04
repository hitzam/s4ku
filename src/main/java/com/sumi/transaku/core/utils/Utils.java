package com.sumi.transaku.core.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sumi.transaku.core.domains.ApplicationProperties;
import com.sumi.transaku.core.domains.Sell;
import com.sumi.transaku.core.domains.StoreInventory;
import com.sumi.transaku.core.domains.SupplierInventory;
import com.sumi.transaku.core.domains.SupplierPurchaseOrder;
import com.sumi.transaku.core.repositories.ApplicationPropertiesRepository;
import com.sumi.transaku.core.repositories.CustomerRepository;
import com.sumi.transaku.core.repositories.SellRepository;
import com.sumi.transaku.core.repositories.StoreInventoryRepository;
import com.sumi.transaku.core.repositories.SupplierInventoryRepository;
import com.sumi.transaku.core.repositories.SupplierPurchaseOrderRepository;
import com.sumi.transaku.core.repositories.SupplierRepository;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

@Service
@Configurable
public class Utils {
	private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

	@Autowired
	ApplicationPropertiesRepository propertiesRepository;
	
	@Autowired
	StoreInventoryRepository inventoryRepository;

	@Autowired
	SupplierInventoryRepository supplierInventoryRepository;

	@Autowired
	SellRepository sellRepository;

	@Autowired
	SupplierPurchaseOrderRepository orderRepository;
	
	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	SupplierRepository supplierRepository;
	
	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}
	
	public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public SimpleDateFormat sdtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public SimpleDateFormat sdhf = new SimpleDateFormat("yyyy-MM-dd HH");
	public SimpleDateFormat sdhmf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public SimpleDateFormat sdfCompact = new SimpleDateFormat("yyMMdd");
	
	public enum TimeSelect {
	    DAY(2),  //calls constructor with value 2
	    HOUR   (1)   //calls constructor with value 1
	    ; // semicolon needed when fields / methods follow

	    private final int time;

	    TimeSelect(int time) {
	        this.time = time;
	    }
	    
	    public int getLevelCode() {
	        return this.time;
	    }
	    
	}
	/*public void preConfigure() {
		//Utils utils = new Utils();
		ApplicationProperties prop = propertiesRepository.findByName(StaticFields.SMS_MASKING_URL);
		StaticFields.SMS_MASKING_URL_VALUE = prop.getStringValue();
		
		prop = propertiesRepository.findByName(StaticFields.SMS_MASKING_USER_KEY);
		StaticFields.SMS_MASKING_USER_KEY_VALUE = prop.getStringValue();
		
		prop = propertiesRepository.findByName(StaticFields.SMS_MASKING_PASS_KEY);
		StaticFields.SMS_MASKING_PASS_KEY_VALUE = prop.getStringValue();
		
		createSystemDirectory();
		
	}*/
	
	public Date generateSellerTrialPeriodDate(int isSignup) {
		ApplicationProperties prop = null;
		if(isSignup == 1){
			prop = propertiesRepository.findByName(StaticFields.SELLER_SIGNUP_PERIOD);
		}else{
			prop = propertiesRepository.findByName(StaticFields.SELLER_TRIAL_PERIOD);
		}
		/*Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, prop.getIntValue());*/
		Date res = setSpecificDate(prop.getIntValue(), TimeSelect.DAY);
		
		return res;
	}
	
	public Date generateBuyerCodeExpired() {
		ApplicationProperties prop = propertiesRepository.findByName(StaticFields.BUYER_CODE_EXPIRED);
		Date res = setSpecificDate(prop.getIntValue(), TimeSelect.HOUR);
		
		return res;
	}
	
	public static String base64ToFile(String strBase64, String location, String prefix, String fileName) {
		String path = "";
		//LOGGER.info("strBase64: {}", strBase64);
		byte[] data = Base64.decode(strBase64);
		
		File folder = new File(location);
		if(!folder.exists()){
			System.out.println("folder not exist");
			boolean res = folder.mkdirs();
			System.out.println("create folder success? "+res);
		}
		
		try{
			path = location+prefix+fileName+".jpg";
			OutputStream stream = new FileOutputStream(path);
			LOGGER.info("data length: {}", data.length);
			stream.write(data);
			stream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prefix+fileName+".jpg";
	}
	
	public static String base64ToTmpFile(String strBase64) {
		try{
			File file = File.createTempFile(String.valueOf(new Date().getTime()), ".jpg");
			//LOGGER.info("strBase64: {}", strBase64);
			byte[] data = Base64.decode(strBase64);
			OutputStream stream = new FileOutputStream(file);
			LOGGER.info("data length: {}", data.length);
			stream.write(data);
			stream.close();
			return file.getPath();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void createSystemDirectory() {
		String[] locations = {StaticFields.PATH_ID_CARD(),
				StaticFields.PATH_CUSTOMER_PICTURE(), StaticFields.PATH_SUPPLIER_PICTURE()};
		
		for (String location : locations) {			
			File folder = new File(location);
			if(!folder.exists()){
				System.out.println("folder not exist");
				boolean res = folder.mkdirs();
				LOGGER.info("Create folder {} success? {}",location,res);
			}
		}
		
	}

	/**
	 * @param value
	 * @param time (1=hour, 2=day)
	 * @return
	 */
	public Date setSpecificDate(int value, TimeSelect time){
		Calendar cal = Calendar.getInstance();
		
		if(time.time == 1){
			cal.add(Calendar.HOUR, value);
		}else if(time.time == 2){
			cal.add(Calendar.DAY_OF_MONTH, value);
		}
		return cal.getTime();
	}
	
	/*public String generateNomorCustomer(){
		LOGGER.info("generateNomorPermintaan invoked.");

    	String code = StaticFields.NOMOR_PERMINTAAN_PREFIX;
    	int currVal = 0;
    	
    	ApplicationProperties prop = propertiesRepository.findByName(StaticFields.NOMOR_PERMINTAAN_SEQUENCE); 
    	currVal = (int)prop.getNumericValue();
		code += Utils.sdfCompact.format(new Date());
		
		try {
			if(prop.getUpdatedDate().compareTo(Utils.sdf.parse(Utils.sdf.format(new Date())))==0){
				code += String.format("%04d", currVal+1);
				
				prop.setUpdatedDate(new Date());
				prop.setNumericValue(currVal+1);
				propertiesRepository.save(prop);
			}else{
				code += String.format("%04d", 1);
				
				prop.setUpdatedDate(new Date());
				prop.setNumericValue(1);
				propertiesRepository.save(prop);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return code;
	}*/
	
	public String generateInventoryCode(int customerId){
		LOGGER.info("generateInventoryCode invoked.");
		String prefix = String.valueOf(customerId);
		//String prefix = String.format("%06d", customerId);
		StoreInventory inv = inventoryRepository.findTopByOrderByIdDesc();
		if(inv == null)
			return prefix+String.format("%06d", 1);
		else
			return prefix+String.format("%06d", (inv.getId()+1));
	}
	
	public String generateSupplierInventoryCode(int customerId){
		LOGGER.info("generateInventoryCode invoked.");
		String prefix = String.valueOf(customerId);
		//String prefix = String.format("%06d", customerId);
		SupplierInventory inv = supplierInventoryRepository.findTopByOrderByIdDesc();
		if(inv == null)
			return prefix+String.format("%06d", 1);
		else
			return prefix+String.format("%06d", (inv.getId()+1));
	}
	
	public String generateSellCode(int sellerId){
		LOGGER.info("generateSellCode invoked.");
		String prefix = StaticFields.SELL_CODE_PREFIX+String.valueOf(sellerId);
		//String prefix = String.format("%06d", customerId);
		Sell inv = sellRepository.findTopBySellerOrderByIdDesc(customerRepository.findOne(sellerId));
		if(inv == null)
			return prefix+String.format("%06d", 1);
		else
			return prefix+String.format("%06d", (inv.getId()+1));
	}
	
	public String generatePurchaseOrderCode(int supplierId){
		LOGGER.info("generatePurchaseOrderCode invoked.");
		String prefix = StaticFields.PURCHASE_ORDER_CODE_PREFIX+String.valueOf(supplierId);
		//String prefix = String.format("%06d", customerId);
		SupplierPurchaseOrder po = orderRepository.findTopBySupplierOrderByIdDesc(supplierRepository.findOne(supplierId));
		if(po == null)
			return prefix+String.format("%06d", 1);
		else
			return prefix+String.format("%06d", (po.getId()+1));
	}
	
	
	public String generateVerificationCode(){
		LOGGER.info("generateVerificationCode invoked.");
		return RandomStringUtils.randomAlphanumeric(StaticFields.VERIFICATION_CODE_LENGTH).toUpperCase();
	}

	public String generatePassword(){
		LOGGER.info("generatePassword invoked.");
		return RandomStringUtils.randomAlphanumeric(StaticFields.PASSWORD_CODE_LENGTH);
	}
	
	public static String objectToJsonString(Object obj) {
		String res = null;

		ObjectMapper mapper = new ObjectMapper();
		try {
			res = mapper.writeValueAsString(obj);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return res;
	}


	public static void stringToFile(String str) {

		FileOutputStream fop = null;
		File file;
		// String content = "This is the text content";

		try {

			file = new File("c:/WORKSPACE/newfile.txt");
			fop = new FileOutputStream(file);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] contentInBytes = str.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//public String encryptSHA512(String stringToHash, String salt){
	public String encryptSHA512(String stringToHash){
		String generatedPassword = null;
		    try {
		         MessageDigest md = MessageDigest.getInstance("SHA-512");
		         //md.update(salt.getBytes("UTF-8"));
		         byte[] bytes = md.digest(stringToHash.getBytes("UTF-8"));
		         StringBuilder sb = new StringBuilder();
		         for(int i=0; i< bytes.length ;i++){
		            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		         }
		         generatedPassword = sb.toString();
		        } 
		       catch (NoSuchAlgorithmException e){
		        e.printStackTrace();
		       } catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		    return generatedPassword;
		}

	public double measurePoints(double mainLat, double mainLong, double destLat, double destLong) {
        // TODO Auto-generated method stub
        final int R = 6371; // Radious of the earth
        Double lat1 = mainLat;
        Double lon1 = mainLong;
        Double lat2 = destLat;
        Double lon2 = destLong;
        Double latDistance = toRad(lat2-lat1);
        Double lonDistance = toRad(lon2-lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + 
                   Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * 
                   Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double distance = R * c;
         
        return distance;
    }
	
	private static Double toRad(Double value) {
		return value * Math.PI / 180;
	}
	
	
	//AES ENCRYPTION
	private static SecretKeySpec secretKey;
    private static byte[] key;
 
	public static void setKey(String myKey) {
		MessageDigest sha = null;
		try {
			key = myKey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public String encrypt(String strToEncrypt, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.encode(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
		}
		return null;
	}

	public String decrypt(String strToDecrypt, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.decode(strToDecrypt)));
		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
		}
		return null;
	}
}
