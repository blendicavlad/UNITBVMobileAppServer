package com.vlad.app.config;

import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.SpringHandlerInstantiator;

@Configuration
public class SerializationConfig {

	@Bean
	public HandlerInstantiator handlerInstantiator(ApplicationContext applicationContext) {
		return new SpringHandlerInstantiator(applicationContext.getAutowireCapableBeanFactory());
	}

	@Bean
	public Jackson2ObjectMapperBuilder objectMapperBuilder(HandlerInstantiator handlerInstantiator) {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
		builder.handlerInstantiator(handlerInstantiator);
		return builder;
	}
}
