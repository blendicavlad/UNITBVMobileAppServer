package com.vlad.app.security;

import com.vlad.app.model.User;
import com.vlad.app.utils.BeanProvider;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<User> {

	SecurityContextProvider securityContextProvider;

	@Override public Optional<User> getCurrentAuditor() {
		securityContextProvider = BeanProvider.getBean(SecurityContextProvider.class);
		return Optional.of(securityContextProvider.getCurrentContextUser());
	}
}
