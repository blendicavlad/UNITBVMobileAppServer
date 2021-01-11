package com.vlad.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvRecurse;
import com.vlad.app.csvconverters.GroupConverter;
import com.vlad.app.jsonconverters.GroupJSONConverter;
import com.vlad.app.jsonconverters.UserJSONConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "students")
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"grades","user"})
public class Student extends Auditable<User> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 13, nullable = false, unique = true)
	@NotBlank(message = "CNP is mandatory")
	@CsvBindByName
	private String CNP;

	@OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<Grade> grades = Set.of();

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", referencedColumnName = "id",nullable = false)
	@CsvRecurse
	@JsonSerialize(converter = UserJSONConverter.class)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="studentgroup_id", referencedColumnName = "id", nullable = false)
	@CsvCustomBindByName(required = true, converter = GroupConverter.class)
	@JsonSerialize(converter = GroupJSONConverter.class)
	private Group group;

}
