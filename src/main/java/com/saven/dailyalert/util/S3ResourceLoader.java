package com.saven.dailyalert.util;

import com.amazonaws.services.s3.AmazonS3Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class S3ResourceLoader {
    @Autowired
    private AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.bucket.file}")
    private String file;

    @Value("${batch.datetime.pattern}")
    private String dateFormat;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    public Resource[] getResources() throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat); //-hh:mma
        String dateTime = LocalDateTime.now().format(formatter);
        Resource[] resources =  this.resourcePatternResolver.getResources("s3://"+bucket+"/"+dateTime+"/"+file);
        return resources;
    }
}
