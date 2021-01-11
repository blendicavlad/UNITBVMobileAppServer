package com.vlad.app.security;

import com.vlad.app.model.User;
import com.vlad.app.repository.UserRepository;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextProvider {

	UserRepository userRepository;

	public SecurityContextProvider(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public UserPrincipal getCurrentUserPrincipal() {
		var auth =  (AbstractAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		return (UserPrincipal) auth.getPrincipal();
	}

	public User getCurrentContextUser() throws RuntimeException {
		var principal = getCurrentUserPrincipal();
		return userRepository.findById(principal.getId()).orElseThrow(
				() -> new RuntimeException("SECURITY VIOLATION: Could not determine current user!")
		);
	}
}
