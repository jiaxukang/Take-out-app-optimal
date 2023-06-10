package com.itk.takeout.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonConfig {

    @Value("${amazon.s3bucket.accessKey}")
    private String accessKey;

    @Value("${amazon.s3bucket.secretKey}")
    private String secretKey;

    @Value("${amazon.s3bucket.region}")
    private String region;



    /**
     * Fluent builder for AmazonS3.
     * Capable of building synchronous and asynchronous clients.
     *
     * @return the AmazonS3 clients
     */
    @Bean
    public AmazonS3 s3() {
        AWSCredentials awsCredentials =
                new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}