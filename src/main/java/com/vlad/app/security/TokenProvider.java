package com.vlad.app.security;

import com.vlad.app.config.AuthProperties;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenProvider {

	private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

	private final AuthProperties authProperties;

	public TokenProvider(AuthProperties authProperties) {
		this.authProperties = authProperties;
	}

	public String createToken(Authentication authentication) {
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

		var now = new Date();
		var expiryDate = new Date(now.getTime() + authProperties.getAuth().getTokenExpirationMsec());

		return Jwts.builder()
				.setClaims(new HashMap<>() {
					{
						put("ROLES", userPrincipal.getAuthorities());
						put("USER_NAME", userPrincipal.getUser().getFirstName() + " " + userPrincipal.getUser().getLastName());
						put("EMAIL", userPrincipal.getEmail());
					}
				})
				.setSubject(Long.toString(userPrincipal.getId()))
				.setIssuedAt(new Date())
				.setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, authProperties.getAuth().getTokenSecret())
				.compact();
	}

	public Long getUserIdFromToken(String token) {
		var claims = Jwts.parser()
				.setSigningKey(authProperties.getAuth().getTokenSecret())
				.parseClaimsJws(token)
				.getBody();

		return Long.parseLong(claims.getSubject());
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(authProperties.getAuth().getTokenSecret()).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException ex) {
			logger.error("Invalid JWT signature");
		} catch (MalformedJwtException ex) {
			logger.error("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			logger.error("Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			logger.error("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			logger.error("JWT claims string is empty.");
		}
		return false;
	}
}
