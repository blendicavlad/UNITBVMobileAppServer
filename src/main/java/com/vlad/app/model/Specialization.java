package com.vlad.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.vlad.app.csvconverters.DepartmentConverter;
import com.vlad.app.jsonconverters.DepartmentJSONConverter;
import com.vlad.app.jsonconverters.DisciplineSetJSONConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
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
@Table(name = "specializations")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(of = {"name"})
public class Specialization {

	@Id
	@Column(length = 10)
	@CsvBindByName
	private String id;

	@Column(length = 60, nullable = false)
	@CsvBindByName
	@NonNull
	private String name;

	@OneToMany(mappedBy = "specialization", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@JsonSerialize(converter = DisciplineSetJSONConverter.class)
	private Set<Discipline> disciplines = Set.of();

	@OneToMany(mappedBy = "specialization", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@JsonIgnore
	private Set<Class> classes = Set.of();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="department_id", referencedColumnName = "id", nullable = false)
	@CsvCustomBindByName(required = true, converter = DepartmentConverter.class)
	@JsonSerialize(converter = DepartmentJSONConverter.class)
	private Department department;

	@PreRemove
	private void removeDependencies() {
		department.getSpecializations().remove(this);
	}

}
