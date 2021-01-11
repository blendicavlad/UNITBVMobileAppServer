package com.vlad.app.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.vlad.app.csvconverters.ProfessorConverter;
import com.vlad.app.csvconverters.SpecializationConverter;
import com.vlad.app.deserializer.DisciplineSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "disciplines")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(of = {"name"})
@JsonSerialize(using = DisciplineSerializer.class)
public class Discipline extends Auditable<User> {

	@Id
	@CsvBindByName
	@Column(length = 10)
	private String id;

	@Column(nullable = false, length = 60)
	@CsvBindByName
	private String name;

	@Column(nullable = false, length = 100)
	@CsvBindByName
	private String description;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "professors_id", referencedColumnName = "id", nullable = false)
	@CsvCustomBindByName(required = true, converter = ProfessorConverter.class)
	private Professor professor;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="specialization_id",referencedColumnName = "id", nullable = false)
	@CsvCustomBindByName(required = true, converter = SpecializationConverter.class)
	private Specialization specialization;

	@JsonBackReference
	@OneToMany(mappedBy = "discipline", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<Exam> exams = Set.of();


	@PreRemove
	private void removeDependencies() {
		specialization.getDisciplines().remove(this);
		professor.getDisciplines().remove(this);
	}
}
