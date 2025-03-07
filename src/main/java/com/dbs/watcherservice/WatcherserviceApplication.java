package com.dbs.watcherservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients
public class WatcherserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WatcherserviceApplication.class, args);
	}

}
