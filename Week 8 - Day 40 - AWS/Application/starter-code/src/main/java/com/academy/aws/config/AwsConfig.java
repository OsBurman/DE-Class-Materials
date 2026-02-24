package com.academy.aws.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * AWS configuration.
 *
 * TODO Task 1: Review and understand the S3Client bean.
 * DefaultCredentialsProvider looks for credentials in this order:
 * 1. Environment variables (AWS_ACCESS_KEY_ID / AWS_SECRET_ACCESS_KEY)
 * 2. ~/.aws/credentials file
 * 3. EC2/ECS instance profile (used in production on AWS)
 */
@Configuration
public class AwsConfig {

    @Value("${aws.region}")
    private String region;

    // TODO Task 1: This bean is COMPLETE — study how credentials are loaded
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
