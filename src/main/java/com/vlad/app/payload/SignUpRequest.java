package com.vlad.app.payload;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.vlad.app.model.UserType;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class SignUpRequest {
	private @NotBlank String firstName;
	private @NotBlank String lastName;
	private @NotBlank @Email String email;
	private @NotBlank String password;
	private @NotBlank UserType userType;
	private String CNP;
	private String Group;

	public SignUpRequest(@NotBlank
			String firstName, @NotBlank
			String lastName, @NotBlank
	@Email
			String email, @NotBlank
			String password, @NotBlank
			UserType userType, String CNP, String Group) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.userType = userType;
		this.CNP = CNP;
		this.Group = Group;
	}

	public SignUpRequest() {
	}

	public @NotBlank String firstName() {
		return firstName;
	}

	public @NotBlank String lastName() {
		return lastName;
	}

	public @NotBlank @Email String email() {
		return email;
	}

	public @NotBlank String password() {
		return password;
	}

	public @NotBlank UserType userType() {
		return userType;
	}

	public String CNP() {
		return CNP;
	}

	public String Group() {
		return Group;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		var that = (SignUpRequest) obj;
		return Objects.equals(this.firstName, that.firstName) &&
				Objects.equals(this.lastName, that.lastName) &&
				Objects.equals(this.email, that.email) &&
				Objects.equals(this.password, that.password) &&
				Objects.equals(this.userType, that.userType) &&
				Objects.equals(this.CNP, that.CNP) &&
				Objects.equals(this.Group, that.Group);
	}

	@Override
	public int hashCode() {
		return Objects.hash(firstName, lastName, email, password, userType, CNP, Group);
	}

	@Override
	public String toString() {
		return "SignUpRequest[" +
				"firstName=" + firstName + ", " +
				"lastName=" + lastName + ", " +
				"email=" + email + ", " +
				"password=" + password + ", " +
				"userType=" + userType + ", " +
				"CNP=" + CNP + ", " +
				"Group=" + Group + ']';
	}

}
