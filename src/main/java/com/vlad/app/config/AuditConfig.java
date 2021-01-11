package com.vlad.app.config;

import com.vlad.app.model.User;
import com.vlad.app.repository.UserRepository;
import com.vlad.app.security.AuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class
AuditConfig {

	final UserRepository userRepository;

	public AuditConfig(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Bean
	public AuditorAware<User> auditorProvider() throws Exception {
		return new AuditorAwareImpl();
	}
}