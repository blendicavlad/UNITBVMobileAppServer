package com.vlad.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "users", uniqueConstraints = {
		@UniqueConstraint(columnNames = "email")
})
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NonNull
	@Column(nullable = false, name = "first_name")
	@NotBlank(message = "First name is mandatory")
	@CsvBindByName
	private String firstName;

	@NonNull
	@Column(nullable = false, name = "last_name")
	@NotBlank(message = "Last name is mandatory")
	@CsvBindByName
	private String lastName;

	@NonNull
	@Email
	@Column(nullable = false, unique = true)
	@CsvBindByName
	private String email;

	private String imageUrl;

	@Column(nullable = false)
	private Boolean email_verified = false;

	@NonNull
	@JsonIgnore
	@Column(nullable = false)
	private String password;

	@NonNull
	@Enumerated(EnumType.STRING)
	@CsvBindByName
	private AuthProvider provider;

	@Column
	@NonNull
	@CsvBindByName
	private boolean isAdmin = false;

	@NonNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserType userType;

	public String getFullName() {
		return getFirstName() + " " + getLastName();
	}
}
