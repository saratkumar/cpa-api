package com.dbs.watcherservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan({"org.springframework.mail.*"})
public class WatcherserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WatcherserviceApplication.class, args);
	}

}
