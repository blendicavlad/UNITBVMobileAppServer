package com.vlad.app.payload;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record AuthResponse(String accessToken, String tokenType) {
	public AuthResponse(String accessToken) {
		this(accessToken, OAuth2AccessToken.TokenType.BEARER.getValue());
	}
}
