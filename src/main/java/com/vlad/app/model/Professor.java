package com.vlad.app.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvRecurse;
import com.vlad.app.jsonconverters.DisciplineSetJSONConverter;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "professors")
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"disciplines", "user"})
public class Professor extends Auditable<User> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 13, nullable = false, unique = true)
	@NotBlank(message = "CNP is mandatory")
	@CsvBindByName
	private String CNP;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
	@CsvRecurse
	private User user;

	@OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@JsonSerialize(converter = DisciplineSetJSONConverter.class)
	private Set<Discipline> disciplines = Set.of();
}
