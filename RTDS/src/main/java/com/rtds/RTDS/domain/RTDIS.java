package com.rtds.RTDS.domain;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class RTDIS {

    private static String API_URL = "";
    //@Value("${topic_name}")
    private static String KAFKA_TOPIC = "topic_one";


    @Autowired
    private KafkaTemplate<String,String> kafkaProducer;

    private ScheduledExecutorService scheduler;

    public void startStreaming() {
        scheduler = Executors.newSingleThreadScheduledExecutor();

        // Schedule the stream method to run every 5 seconds
        scheduler.scheduleAtFixedRate(this::stream, 0, 10, TimeUnit.SECONDS);
    }

    public void stopStreaming() {
        scheduler.shutdown();
    }

    public void stream() {
        WebClient webClient = WebClient.create();
        String ablyUrl = "https://api.blockchain.com/v3/exchange/l2/BTC-USD"; // The URL to retrieve Bitcoin price

        webClient.get()
                .uri(ablyUrl)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(data -> {
                    System.out.println("Received msg: " + data);
                    kafkaProducer.send(KAFKA_TOPIC, data);
                    System.out.println("Event sent to Kafka: " + data);
                });
    }
    public void sendMessage(){

        kafkaProducer.send(KAFKA_TOPIC,"Hello");
    }

}
