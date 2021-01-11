package com.vlad.app.security;


import com.vlad.app.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class UserPrincipal implements OAuth2User, UserDetails {

	private Long id;
	private String email;
	private String password;
	private User user;
	private Collection<? extends GrantedAuthority> authorities;
	private Map<String, Object> attributes;

	public UserPrincipal(User user, List<GrantedAuthority> authorities) {
		this.user = user;
		this.id = user.getId();
		this.email = user.getEmail();
		this.password = user.getPassword();
		this.authorities = authorities;
	}

	public static UserPrincipal create(User user) {

		List<GrantedAuthority> authorities = switch (user.getUserType()) {
			case STUDENT -> List.of(
					new SimpleGrantedAuthority("ROLE_STUDENT"));
			case ADMIN -> List.of(
					new SimpleGrantedAuthority("ROLE_ADMIN"));
			case PROFESSOR -> List.of(
					new SimpleGrantedAuthority("ROLE_PROFESSOR"));
		};

		return new UserPrincipal(
				user,
				authorities
		);
	}

	public static UserPrincipal create(User user, Map<String, Object> attributes) {
		var userPrincipal = UserPrincipal.create(user);
		userPrincipal.setAttributes(attributes);
		return userPrincipal;
	}

	@Override public String getName() {
		return id != null ? id.toString() : null;
	}

	@Override public String getUsername() {
		return email;
	}

	@Override public boolean isAccountNonExpired() {
		return true;
	}

	@Override public boolean isAccountNonLocked() {
		return true;
	}

	@Override public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override public boolean isEnabled() {
		return true;
	}
}
