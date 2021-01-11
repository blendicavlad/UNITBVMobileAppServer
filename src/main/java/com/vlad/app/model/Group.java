package com.vlad.app.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.vlad.app.csvconverters.ClassConverter;
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
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "studentgroups")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(of = {"id"})
public class Group extends Auditable<User> {

	@Id
	@CsvBindByName
	@Column(length = 10)
	private String id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="class_id", referencedColumnName = "id", nullable = false)
	@CsvCustomBindByName(required = true, column = "class", converter = ClassConverter.class)
	@JsonBackReference
	private Class clss;

	@JsonIgnore
	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<Student> students = Set.of();

}
