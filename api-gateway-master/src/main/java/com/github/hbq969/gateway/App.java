package com.github.hbq969.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = {KafkaAutoConfiguration.class})
@EnableCaching
@EnableTransactionManagement(proxyTargetClass = true)
@EnableScheduling
public class App {

  public static void main(String[] args) {
    System.setProperty("jasypt.encryptor.password","U(^3ia)*v2$");
    SpringApplication.run(App.class, args);
  }

}
