package com.sumi.transaku.core.utils;

import org.springframework.context.annotation.Configuration;


@Configuration
public class StaticFields {
	
	/*1=LINUX, 2=WINDOWS*/
	public static int SERVER_OS;
	
	public static final int RESPONSE_CODE_DATA_NOT_FOUND = 404;
	public static final int RESPONSE_CODE_SUCCESS = 200;
	public static final int RESPONSE_CODE_FAILED = 500;
	public static final int RESPONSE_CODE_UNAUTHORIZED = 401;
	public static final int RESPONSE_CODE_BAD_REQUEST = 400;

	public static final int LOGIN_STATUS_TRUE = 1;
	public static final int LOGIN_STATUS_FALSE = 2;

	public static final int SESSION_EXPIRED_TIME = 12;

	public static final String MOBILE_APP_VERSION = "MOBILE_APP_VERSION";
	public static final String REPORT_ENCRYPTION_SECRET_KEY = "Kunc1*?t124N5aku";
	
	/*STATUS*/
	public static final int STATUS_DISABLED = 0;
	public static final int STATUS_ENABLED = 1;
	public static final int STATUS_REMOVED = 2;

	public static final int STATUS_USER_INACTIVE = 0;
	public static final int STATUS_USER_ACTIVE = 1;

	public static final int STATUS_CUSTOMER_REGISTERED = 1;
	public static final int STATUS_CUSTOMER_VERIFIED = 2;
	public static final int STATUS_CUSTOMER_SURVEYED = 3;
	public static final int STATUS_CUSTOMER_SUSPENDED = 4;
	public static final int STATUS_CUSTOMER_CUT = 5;
	
	public static final int STATUS_SUPPLIER_DISABLED = 0;
	public static final int STATUS_SUPPLIER_REGISTERED = 1;
	public static final int STATUS_SUPPLIER_VERIFIED = 2;
	public static final int STATUS_SUPPLIER_SURVEYED = 3;

	public static final int STATUS_SUPPLIER_SELLER = 6;
	
	public static final int STATUS_PURCHASE_ORDER_REGISTERED = 1;
	public static final int STATUS_PURCHASE_ORDER_SENT_TO_SUPPLIER = 2;
	public static final int STATUS_PURCHASE_ORDER_ON_PROCESS = 3;
	public static final int STATUS_PURCHASE_ORDER_FINISHED = 4;
	public static final int STATUS_PURCHASE_ORDER_CANCELED = 5;
	//public static final int STATUS_SUPPLIER_SUSPENDED = 4;
	//public static final int STATUS_SUPPLIER_CUT = 5;

	public static final int ROLE_ADMIN = 1;
	public static final int ROLE_FINANCE = 2;
	public static final int ROLE_HR = 3;
	public static final int ROLE_GA = 4;
	public static final int ROLE_OPERATOR = 5;
	public static final int ROLE_SELLER = 6;
	public static final int ROLE_BUYER = 7;

	/*MAIL*/
	public static final String EMAIL_CUSTOMER_REGISTER_SUBJECT = "Registrasi TRANSAKU Berhasil";
	public static final String EMAIL_CUSTOMER_VERIFIED_CONTENT_OLD = "Akun TRANSAKU anda telah terverifikasi. Gunakan email: xxxx dan password: **** untuk login.";
	public static final String EMAIL_CUSTOMER_VERIFIED_CONTENT = "Akun TRANSAKU anda telah terverifikasi. Gunakan email/nomor hp untuk login.";
	public static final String EMAIL_CUSTOMER_SURVEYED_CONTENT = "Selamat, setelah proses survey oleh team kami, status akun anda telah kami update menjadi full-version";
	public static final String EMAIL_CUSTOMER_REGISTER_CONTENT = "Terimakasih telah mendaftar TRANSAKU. Data anda sedang diverifikasi, mohon untuk menunggu.";


	public static final String EMAIL_INFORMASI_SUBJECT = "Notifikasi Permintaan Informasi ";
	public static final String EMAIL_CONTENT_INFORMASI_NEW_REQUEST = "Permintaan Informasi telah kami terima, nomor permintaan anda adalah: ";
	public static final String EMAIL_CONTENT_INFORMASI_DISPOSISI = "Status permintaan Informasi dengan nomor permintaan xxxx telah diperbarui menjadi: ";
	public static final String EMAIL_CONTENT_INFORMASI_SELESAI = "Status permintaan informasi dengan nomor permintaan xxxx telah selesai. Silahkan periksa melalui website untuk lebih detail. ";
	public static final String EMAIL_KEBERATAN_SUBJECT = "Notifikasi Pengajuan Keberatan ";
	public static final String EMAIL_CONTENT_KEBERATAN_NEW_REQUEST = "Pengajuan Keberatan atas nomor permintaan xxxx telah kami terima.";
	public static final String EMAIL_CONTENT_KEBERATAN_DISPOSISI = "Status Pengajuan Keberatan dengan nomor permintaan xxxx telah diperbarui menjadi: ";
	public static final String EMAIL_CONTENT_KEBERATAN_SELESAI = "Status pengajuan kebaratan dengan nomor permintaan xxxx telah selesai. Silahkan periksa melalui website untuk lebih detail. ";
	
	/*SMS*/
	public static final String SMS_CUSTOMER_REGISTER_CONTENT = "Terimakasih telah mendaftar TRANSAKU. Data anda sedang diverifikasi, mohon untuk menunggu.";
	public static final String SMS_CUSTOMER_REGISTER_BUYER_CONTENT = "Terimakasih telah mendaftar TRANSAKU. Gunakan kode berikut untuk aktivasi akun anda: xxxxxx";
	public static final String SMS_CUSTOMER_VERIFIED_CONTENT = "Akun TRANSAKU anda telah terverifikasi. Gunakan email: xxxx dan password: **** untuk login.";
	public static final String SMS_CUSTOMER_RESET_PASSWORD = "Reset Password Berhasil.\n Gunakan password berikut untuk login:\n{password}";
	
	/*APPLICATION PROPERTIES*/
	public static final String SELLER_SIGNUP_PERIOD = "SELLER_SIGNUP_PERIOD";
	public static final String SELLER_TRIAL_PERIOD = "SELLER_TRIAL_PERIOD";
	public static final String BUYER_CODE_EXPIRED = "BUYER_CODE_EXPIRED";
	public static final String SMS_MASKING_URL = "SMS_MASKING_URL";
	public static final String SMS_MASKING_USER_KEY = "SMS_MASKING_USER_KEY";
	public static final String SMS_MASKING_PASS_KEY = "SMS_MASKING_PASS_KEY";
//	public static String SMS_MASKING_URL_VALUE = "https://reguler.zenziva.net/apps/smsapi.php?userkey=[userkey]&passkey=[passkey]&nohp=[nohp]&pesan=[pesan]";
//	public static String SMS_MASKING_USER_KEY_VALUE = "f7gbm6";
//	public static String SMS_MASKING_PASS_KEY_VALUE = "sumiDev01";
	public static String SMS_MASKING_URL_VALUE = "";
	public static String SMS_MASKING_USER_KEY_VALUE = "";
	public static String SMS_MASKING_PASS_KEY_VALUE = "";

	public static String SELL_CODE_PREFIX = "TRX";
	public static String PURCHASE_ORDER_CODE_PREFIX = "PO";
	
	public static final int VERIFICATION_CODE_LENGTH = 6;
	public static final int PASSWORD_CODE_LENGTH = 8;

	public static final String TRX_TYPE_CREDIT = "C";
	public static final String TRX_TYPE_DEBIT = "D";

	public static final String AWS_S3_TRANSAKUBACKEND_ACCESS_KEY_ID = "AKIAJC4NAHZRXRHEK66Q";
	public static final String AWS_S3_TRANSAKUBACKEND_SECRET_KEY = "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";
	public static final String AWS_S3_SELLER_URL = "https://s3-ap-southeast-1.amazonaws.com/transaku-seller/";
	public static final String AWS_S3_SELLER_BUCKET = "transaku-seller";
	public static final String AWS_S3_SUPPLIER_URL = "https://s3-ap-southeast-1.amazonaws.com/transaku-supplier/";
	public static final String AWS_S3_SUPPLIER_BUCKET = "transaku-supplier";
	public static final String AWS_S3_STORE_INVENTORY_URL = "https://s3-ap-southeast-1.amazonaws.com/transaku-store-inventory/";
	public static final String AWS_S3_STORE_INVENTORY_BUCKET = "transaku-store-inventory";
	public static final String AWS_S3_SUPPLIER_INVENTORY_URL = "https://s3-ap-southeast-1.amazonaws.com/transaku-supplier-inventory/";
	public static final String AWS_S3_SUPPLIER_INVENTORY_BUCKET = "transaku-supplier-inventory";
	
	public static String PATH_ID_CARD(){
		String path;
		if(SERVER_OS==1)
			path = "transaku/images/id-card/";
		else
			path = "C:/transaku/images/id-card/";
		
		return path;
	}
	
	public static String PATH_CUSTOMER_PICTURE(){
		String path;
		if(SERVER_OS==1)
			path = "transaku/images/customer-picture/";
		else
			path = "C:/transaku/images/customer-picture/";
		
		return path;
	}
	
	public static String PATH_SUPPLIER_PICTURE(){
		String path;
//		System.out.println("SERVER_OS: "+SERVER_OS);
//		System.out.println("SERVER_OS: "+System.getProperty("os.name"));
		if(SERVER_OS==1)
			path = "transaku/images/supplier-picture/";
		else
			path = "C:/transaku/images/supplier-picture/";
		
		return path;
	}
	
	
}
