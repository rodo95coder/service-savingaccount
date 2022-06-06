package com.nttdata.bootcamp;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
	
	@Bean("clientRest")
	public RestTemplate registrarRestTemplate() {
		return new RestTemplate();
	}
	
	@LoadBalanced
    @Bean
	public WebClient.Builder WebClient() {
      return WebClient.builder();
    }
	
}