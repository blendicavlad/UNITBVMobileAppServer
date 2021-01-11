package com.vlad.app.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.vlad.app.csvconverters.SpecializationConverter;
import com.vlad.app.jsonconverters.SpecializationJSONConverter;
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
@Table(name = "classes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(of = {"id"})
public class Class extends Auditable<User> {

	@Id
	@CsvBindByName
	@Column(length = 10)
	private String id;

	@Column(length = 4)
	@CsvBindByName
	private Integer year;

	@OneToMany(mappedBy = "clss", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@JsonManagedReference
	private Set<Group> groups = Set.of();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="specialization_id",referencedColumnName = "id", nullable = false)
	@CsvCustomBindByName(required = true, converter = SpecializationConverter.class)
	@JsonSerialize(converter = SpecializationJSONConverter.class)
	private Specialization specialization;

	@PreRemove
	private void removeDependencies() {
		specialization.getClasses().remove(this);
	}
}
