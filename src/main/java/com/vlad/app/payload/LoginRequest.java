package com.vlad.app.payload;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class LoginRequest {
	@JsonProperty() private @NotBlank @Email String email;
	private @NotBlank String password;

	public LoginRequest(@NotBlank
	@Email
	@JsonProperty()
			String email, @NotBlank
			String password) {
		this.email = email;
		this.password = password;
	}

	public LoginRequest() {
	}

	@JsonProperty() public @NotBlank @Email String email() {
		return email;
	}

	public @NotBlank String password() {
		return password;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		var that = (LoginRequest) obj;
		return Objects.equals(this.email, that.email) &&
				Objects.equals(this.password, that.password);
	}

	@Override
	public int hashCode() {
		return Objects.hash(email, password);
	}

	@Override
	public String toString() {
		return "LoginRequest[" +
				"email=" + email + ", " +
				"password=" + password + ']';
	}

}
