package com.zappos.config;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by maxkeene on 6/11/14.
 */
@Configuration
@PropertySource("classpath:spring.properties")
public class ApplicationConfig {
    @Value("${aws.key}")
    private String accessKey;

    @Value("${aws.secret}")
    private String secretKey;

    @Bean
    public AmazonDynamoDBAsync amazonDynamoDB() {
        AmazonDynamoDBAsyncClient client = new AmazonDynamoDBAsyncClient(new BasicAWSCredentials(accessKey, secretKey));
        client.setRegion(Region.getRegion(Regions.US_WEST_2));
        return client;
    }
}
