package com.saven.dailyalert.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.logging.Logger;

public class AwsS3FileUploader {

    Logger logger = Logger.getLogger(AwsS3FileUploader.class.getName());

    String accessKey;

    String secretKey;

    String bucketName;

    public void createBucket(String bucketName) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3client = new AmazonS3Client(credentials);
        s3client.createBucket(bucketName);
        logger.info("bucket created by name " + bucketName);
    }

    public void upload(InputStream is, String uploadAs) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3client = new AmazonS3Client(credentials);
        s3client.putObject(new PutObjectRequest(bucketName, uploadAs, is, new ObjectMetadata()));
        logger.info("upload complete.");
    }

    public void upload(InputStream is, String uploadAs, String bucketName) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3client = new AmazonS3Client(credentials);
        s3client.putObject(new PutObjectRequest(bucketName, uploadAs, is, new ObjectMetadata()));
        logger.info("upload complete to "+ bucketName+"/"+uploadAs);
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}
