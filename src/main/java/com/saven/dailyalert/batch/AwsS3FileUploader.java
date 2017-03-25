package com.saven.dailyalert.batch;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;
import java.util.logging.Logger;

public class AwsS3FileUploader {

    Logger logger = Logger.getLogger(AwsS3FileUploader.class.getName());

    String accessKey;

    String secretKey;

    String bucketName;

    public void upload(File file, String uploadAs) {

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3client = new AmazonS3Client(credentials);
        s3client.putObject(new PutObjectRequest(bucketName, uploadAs, file));
        logger.info("upload complete.");
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
