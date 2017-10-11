package com.gar.system;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootConfiguration
@Configuration
@ComponentScan
public class GarLauncher {

	private static Logger log = Logger.getLogger(GarLauncher.class);

	public static void main(String[] args) {
		final ApplicationContext ctx = SpringApplication.run(GarLauncher.class, args);
		final String[] beanNames = ctx.getBeanDefinitionNames();
		Arrays.sort(beanNames);
		for (final String beanName : beanNames) {
			log.info(beanName);
		}
	}
}
