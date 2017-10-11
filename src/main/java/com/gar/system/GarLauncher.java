package com.gar.system;

import static com.gar.system.constants.GarConstants.BASE_PACKAGE_NAME;
import static com.gar.system.constants.GarConstants.DAO_PACKAGE_NAME;
import static com.gar.system.constants.GarConstants.MODEL_PACKAGE_NAME;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@SpringBootApplication(exclude={org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class})
@Configuration
@EnableEncryptableProperties
@EnableScheduling
@EnableBatchProcessing
@EnableJpaRepositories(basePackages = { DAO_PACKAGE_NAME })
@EntityScan(basePackages = {MODEL_PACKAGE_NAME})
@ComponentScan(BASE_PACKAGE_NAME)
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
