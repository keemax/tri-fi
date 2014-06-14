package com.zappos.config;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.zappos.dao.TrainingDAO;
import com.zappos.prediction.Predictor;
import com.zappos.prediction.Trainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Arrays;
import java.util.List;

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

    @Value("${google.clientId}")
    private String googleAccessKey;

    @Value("${google.secret}")
    private String googleSecretKey;


    @Bean
    public AmazonDynamoDBAsync amazonDynamoDB() {
        AmazonDynamoDBAsyncClient client = new AmazonDynamoDBAsyncClient(new BasicAWSCredentials(accessKey, secretKey));
        client.setRegion(Region.getRegion(Regions.US_WEST_2));
        return client;
    }


    @Bean
    public Predictor predictor() {
        return new Predictor();
    }


    @Bean
    public TrainingDAO trainingDAO() {
        return new TrainingDAO();
    }

    @Bean
    public Trainer trainer() {
        return new Trainer();
    }

    @Bean(name = "knownRouters")
    public List<String> knownRouters() {

        String[] choiceMac = new String [] {
                "e0:1c:41:04:88:e9",    // 4th floor - zMobile
                "e0:1c:41:04:89:69",
                "e0:1c:41:04:7d:e9",
                "e0:1c:41:04:7d:95",
                "e0:1c:41:04:89:a9",
                "e0:1c:41:04:89:29",
                "24:de:c6:a5:f0:38",    // 4th floor - wpa2
                "24:de:c6:a5:e2:a8",
                "e0:1c:41:04:96:69",     // 3rd floor - zMobile
                "08:ea:44:9c:ff:e9",
                "08:ea:44:9c:fe:e9",
                "08:ea:44:9c:ff:a9",
                "08:ea:44:9c:ff:55",
                "08:ea:44:9c:ff:29"



        };

        String[] strArray =  new String [] {
               // "00:15:ff:7b:32:8b",
                "08:ea:44:90:bb:14",
                "08:ea:44:90:bb:15",
                "08:ea:44:90:bb:28",
                "08:ea:44:90:bb:29",
                "08:ea:44:90:bb:2a",
                "08:ea:44:90:ca:54",
               // "08:ea:44:90:ca:55",
                "08:ea:44:90:ca:68",
                "08:ea:44:90:ca:69",
                "08:ea:44:90:ca:6a",
                "08:ea:44:90:ea:54",
                "08:ea:44:90:ea:55",
                "08:ea:44:90:ea:68",
                "08:ea:44:90:ea:69",
                "08:ea:44:90:ea:6a",
                "08:ea:44:91:2e:55",
                "08:ea:44:91:2e:68",
                "08:ea:44:91:2e:69",
                "08:ea:44:91:2e:6a",
                "08:ea:44:91:4a:d4",
                "08:ea:44:91:4a:d5",
                "08:ea:44:91:4a:e8",
                "08:ea:44:91:4a:e9",
                "08:ea:44:91:4a:ea",
                "08:ea:44:9c:ee:54",
                "08:ea:44:9c:ee:55",
                "08:ea:44:9c:ee:68",
                "08:ea:44:9c:ee:69",
                "08:ea:44:9c:ee:6a",
                "08:ea:44:9c:ef:14",
                "08:ea:44:9c:ef:15",
                "08:ea:44:9c:ef:28",
                "08:ea:44:9c:ef:29",
                "08:ea:44:9c:ef:2a",
                "08:ea:44:9c:ef:95",
                "08:ea:44:9c:ef:a8",
                "08:ea:44:9c:ef:a9",
                "08:ea:44:9c:ef:aa",
                "08:ea:44:9c:fe:e8",
                "08:ea:44:9c:fe:e9",
                "08:ea:44:9c:fe:ea",
                "08:ea:44:9c:ff:28",
                "08:ea:44:9c:ff:29",
                "08:ea:44:9c:ff:2a",
                "08:ea:44:9c:ff:54",
                "08:ea:44:9c:ff:55",
                "08:ea:44:9c:ff:68",
                "08:ea:44:9c:ff:69",
                "08:ea:44:9c:ff:6a",
                "08:ea:44:9c:ff:a8",
                "08:ea:44:9c:ff:a9",
                "08:ea:44:9c:ff:aa",
                "08:ea:44:9c:ff:d4",
                "08:ea:44:9c:ff:d5",
                "08:ea:44:9c:ff:e8",
                "08:ea:44:9c:ff:e9",
                "08:ea:44:9c:ff:ea",
                "24:c9:a1:93:4a:f8",
                "24:c9:a1:93:4a:fc",
                "24:de:c6:a5:e2:a0",
                "24:de:c6:a5:e2:a1",
                "24:de:c6:a5:e2:a2",
                "24:de:c6:a5:e2:a8",
                "24:de:c6:a5:e2:a9",
                "24:de:c6:a5:e2:aa",
                "24:de:c6:a5:f0:30",
                "24:de:c6:a5:f0:31",
                "24:de:c6:a5:f0:32",
                "24:de:c6:a5:f0:38",
                "24:de:c6:a5:f0:39",
                "24:de:c6:a5:f0:3a",
                "24:de:c6:a5:f4:90",
                "24:de:c6:a5:f4:91",
                "24:de:c6:a5:f4:92",
                "24:de:c6:a5:f4:98",
                "24:de:c6:a5:f4:99",
                "24:de:c6:a5:f4:9a",
                "24:de:c6:a5:f4:f0",
                "24:de:c6:a5:f4:f1",
                "24:de:c6:a5:f4:f2",
                "24:de:c6:a5:f4:f8",
                "24:de:c6:a5:f4:f9",
                "24:de:c6:a5:f4:fa",
                "24:de:c6:a5:f5:10",
                "24:de:c6:a5:f5:18",
                "24:de:c6:a5:f5:19",
                "24:de:c6:a5:f5:1a",
                "24:de:c6:a5:fc:30",
                "24:de:c6:a5:fc:31",
                "24:de:c6:a5:fc:32",
                "24:de:c6:a5:fc:38",
                "24:de:c6:a5:fc:39",
                "24:de:c6:a5:fc:3a",
                "24:de:c6:a5:fd:50",
                "24:de:c6:a5:fd:58",
                "24:de:c6:a5:fd:59",
                "24:de:c6:a5:fd:5a",
                "24:de:c6:a5:fd:60",
                "24:de:c6:a5:fd:61",
                "24:de:c6:a5:fd:62",
                "24:de:c6:a5:fd:68",
                "24:de:c6:a5:fd:69",
                "24:de:c6:a5:fd:6a",
               // "24:de:c6:a5:fd:80",
               // "24:de:c6:a5:fd:81",
               // "24:de:c6:a5:fd:88",
               // "24:de:c6:a5:fd:89",
               // "24:de:c6:a5:fd:8a",
                "24:de:c6:a5:fd:d0",
                "24:de:c6:a5:fd:d1",
                "24:de:c6:a5:fd:d2",
                "24:de:c6:a5:fd:d8",
                "24:de:c6:a5:fd:d9",
                "24:de:c6:a5:fd:da",
                "24:de:c6:a5:fd:e2",
                "24:de:c6:a5:fd:e8",
                "24:de:c6:a5:fd:e9",
                "24:de:c6:a5:fd:ea",
                "24:de:c6:a6:03:d0",
                "24:de:c6:a6:03:d8",
                "24:de:c6:a6:03:d9",
                "24:de:c6:a6:03:da",
               // "24:de:c6:a6:03:f0",
               // "24:de:c6:a6:03:f1",
               // "24:de:c6:a6:03:f2",
               // "24:de:c6:a6:03:f8",
               // "24:de:c6:a6:03:f9",
               // "24:de:c6:a6:03:fa",
                "24:de:c6:a6:04:10",
               // "24:de:c6:a6:04:12",
                "24:de:c6:a6:04:18",
                "24:de:c6:a6:04:19",
               // "24:de:c6:a6:04:20",
               // "24:de:c6:a6:04:28",
               // "24:de:c6:a6:04:29",
               // "24:de:c6:a6:04:2a",
               // "24:de:c6:a6:04:78",
               // "24:de:c6:a6:04:79",
               // "24:de:c6:a6:04:7a",
                "24:de:c6:a6:05:10",
                "24:de:c6:a6:05:18",
                "24:de:c6:a6:05:19",
                "24:de:c6:a6:05:1a",
               // "24:de:c6:a6:07:00",
               // "24:de:c6:a6:07:01",
               // "24:de:c6:a6:07:02",
                "24:de:c6:a6:07:b0",
                "24:de:c6:a6:07:b8",
                "24:de:c6:a6:07:b9",
                "24:de:c6:a6:07:ba",
                "24:de:c6:a6:07:d8",
                "24:de:c6:a6:07:d9",
                "24:de:c6:a6:07:da",
                "24:de:c6:a6:08:30",
                "24:de:c6:a6:08:31",
                "24:de:c6:a6:08:32",
                "24:de:c6:a6:08:38",
                "24:de:c6:a6:08:39",
                "24:de:c6:a6:08:3a",
               // "24:de:c6:a6:08:61",
               // "24:de:c6:a6:08:68",
               // "24:de:c6:a6:08:69",
               // "24:de:c6:a6:08:6a",
                "24:de:c6:a6:08:78",
                "24:de:c6:a6:08:79",
                "24:de:c6:a6:08:7a",
                "24:de:c6:a6:08:f0",
                "24:de:c6:a6:08:f1",
                "24:de:c6:a6:08:f2",
                "24:de:c6:a6:08:f8",
                "24:de:c6:a6:08:f9",
                "24:de:c6:a6:08:fa",
                "24:de:c6:a6:09:00",
               // "24:de:c6:a6:09:01",
                "24:de:c6:a6:09:02",
                "24:de:c6:a6:09:08",
                "24:de:c6:a6:09:09",
                "24:de:c6:a6:09:0a",
                "24:de:c6:a6:09:30",
                "24:de:c6:a6:09:38",
                "24:de:c6:a6:09:39",
                "24:de:c6:a6:09:3a",
                "24:de:c6:a6:09:40",
                "24:de:c6:a6:09:41",
                "24:de:c6:a6:09:42",
                "24:de:c6:a6:09:48",
                "24:de:c6:a6:09:49",
                "24:de:c6:a6:09:4a",
                "24:de:c6:a6:0a:00",
                "24:de:c6:a6:0a:08",
                "24:de:c6:a6:0a:09",
                "24:de:c6:a6:0a:0a",
                "24:de:c6:a6:a7:e0",
                "24:de:c6:a6:a7:e1",
               // "24:de:c6:a6:a7:e2",
                "d8:c7:c8:14:80:98",
                "e0:1c:41:04:77:14",
                "e0:1c:41:04:77:15",
                "e0:1c:41:04:77:29",
                "e0:1c:41:04:77:a8",
                "e0:1c:41:04:77:a9",
                "e0:1c:41:04:77:aa",
                "e0:1c:41:04:7d:94",
                "e0:1c:41:04:7d:95",
                "e0:1c:41:04:7d:96",
                "e0:1c:41:04:7d:a8",
                "e0:1c:41:04:7d:a9",
                "e0:1c:41:04:7d:aa",
                "e0:1c:41:04:7d:ab",
                "e0:1c:41:04:7d:d4",
                "e0:1c:41:04:7d:d5",
                "e0:1c:41:04:7d:d6",
                "e0:1c:41:04:7d:e8",
                "e0:1c:41:04:7d:e9",
                "e0:1c:41:04:7d:ea",
                "e0:1c:41:04:7d:eb",
                "e0:1c:41:04:87:54",
                "e0:1c:41:04:87:55",
                "e0:1c:41:04:87:56",
                "e0:1c:41:04:87:68",
                "e0:1c:41:04:87:69",
                "e0:1c:41:04:87:6a",
                "e0:1c:41:04:87:6b",
                "e0:1c:41:04:87:94",
                "e0:1c:41:04:87:95",
                "e0:1c:41:04:87:96",
                "e0:1c:41:04:87:a8",
                "e0:1c:41:04:87:a9",
                "e0:1c:41:04:87:aa",
                "e0:1c:41:04:87:ab",
                "e0:1c:41:04:87:d4",
                "e0:1c:41:04:87:d6",
                "e0:1c:41:04:87:e8",
                "e0:1c:41:04:87:e9",
                "e0:1c:41:04:87:ea",
                "e0:1c:41:04:87:eb",
                "e0:1c:41:04:88:15",
                "e0:1c:41:04:88:16",
                "e0:1c:41:04:88:28",
                "e0:1c:41:04:88:29",
                "e0:1c:41:04:88:2a",
                "e0:1c:41:04:88:2b",
                "e0:1c:41:04:88:54",
                "e0:1c:41:04:88:55",
                "e0:1c:41:04:88:56",
                "e0:1c:41:04:88:68",
                "e0:1c:41:04:88:69",
                "e0:1c:41:04:88:6a",
                "e0:1c:41:04:88:6b",
                "e0:1c:41:04:88:94",
                "e0:1c:41:04:88:96",
                "e0:1c:41:04:88:a8",
                "e0:1c:41:04:88:a9",
                "e0:1c:41:04:88:aa",
                "e0:1c:41:04:88:ab",
                "e0:1c:41:04:88:e8",
                "e0:1c:41:04:88:e9",
                "e0:1c:41:04:88:ea",
                "e0:1c:41:04:88:eb",
                "e0:1c:41:04:89:28",
                "e0:1c:41:04:89:29",
                "e0:1c:41:04:89:2a",
                "e0:1c:41:04:89:2b",
                "e0:1c:41:04:89:54",
                "e0:1c:41:04:89:55",
                "e0:1c:41:04:89:56",
                "e0:1c:41:04:89:68",
                "e0:1c:41:04:89:69",
                "e0:1c:41:04:89:6a",
                "e0:1c:41:04:89:6b",
                "e0:1c:41:04:89:a8",
                "e0:1c:41:04:89:a9",
                "e0:1c:41:04:89:aa",
                "e0:1c:41:04:89:ab",
               // "e0:1c:41:04:92:d4",
               // "e0:1c:41:04:92:d5",
               // "e0:1c:41:04:92:e9",
               // "e0:1c:41:04:92:ea",
                "e0:1c:41:04:96:68",
                "e0:1c:41:04:96:69",
                "e0:1c:41:04:96:6a",
//                "e0:1c:41:04:97:e8",
//                "e0:1c:41:04:97:e9",
//                "e0:1c:41:04:97:ea",
//                "e0:1c:41:04:98:e8",
//                "e0:1c:41:04:98:e9",
//                "e0:1c:41:04:98:ea",
//                "e0:1c:41:04:99:28",
//                "e0:1c:41:04:99:29",
//                "e0:1c:41:04:99:2a",
//                "e0:1c:41:04:99:68",
//                "e0:1c:41:04:99:69",
//                "e0:1c:41:04:99:6a",
//                "e0:1c:41:04:99:d4",
//                "e0:1c:41:04:99:d5",
//                "e0:1c:41:04:99:e8",
//                "e0:1c:41:04:99:e9",
//                "e0:1c:41:04:99:ea",
//                "e0:1c:41:04:9a:a8",
//                "e0:1c:41:04:9a:a9",
//                "e0:1c:41:04:9a:aa",
//                "e0:1c:41:04:9a:e8",
//                "e0:1c:41:04:9a:e9",
//                "e0:1c:41:04:9a:ea",
//                "e0:1c:41:04:a9:28",
//                "e0:1c:41:04:a9:e8",
//                "e0:1c:41:04:a9:e9",
//                "e0:1c:41:04:a9:ea"
        };
        return Arrays.asList(strArray);
    }
}
