package com.sumi.transaku.core.utils;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.sumi.transaku.core.domains.Customer;
import com.sumi.transaku.core.domains.ResponseModel;


@Service
public class AwsS3Util {
	private static final Logger LOGGER = LoggerFactory.getLogger(AwsS3Util.class);
	private static AWSCredentials credentials = new BasicAWSCredentials(StaticFields.AWS_S3_TRANSAKUBACKEND_ACCESS_KEY_ID, StaticFields.AWS_S3_TRANSAKUBACKEND_SECRET_KEY);
	private static AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.AP_SOUTHEAST_1).build();

	/**
	 * @param customer
	 * @param filePath
	 * @param fileName
	 */
	@Async
	public void uploadPicture(String bucket, String filePath, String fileName) {

		File file = new File(filePath);
		
		//String objectKey = "seller-"+customer.getId()+"-"+customer.getName();
		String objectKey = fileName;
		LOGGER.info(file.getPath()+" will be uploaded to aws s3 with name: "+objectKey);
		
		PutObjectResult putResult = s3Client.putObject(new PutObjectRequest(bucket, objectKey, file).withCannedAcl(CannedAccessControlList.PublicRead));
		LOGGER.info(putResult.getETag());

		//return model;
	}
}
