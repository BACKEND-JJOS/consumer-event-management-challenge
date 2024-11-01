package com.ias.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

@Configuration
public class SecretsManagerConfig {

    @Value("${aws.region}")
    private String region;

    @Bean
    public SecretsManagerClient secretsManagerClient(){
       return SecretsManagerClient.builder()
                .region(Region.of(this.region))
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }

}
