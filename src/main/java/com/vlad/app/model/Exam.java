package com.vlad.app.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.vlad.app.deserializers.DisciplineDeserializer;
import lombok.AllArgsConstructor;
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
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "exams")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(of = {"id", "examDate"})
public class Exam extends Auditable<User> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "exam_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date examDate;

	@Column
	private boolean isLast;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "disciplines_id", referencedColumnName = "id", nullable = false)
	@JsonDeserialize(using = DisciplineDeserializer.class)
	private Discipline discipline;

	@OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<Grade> grades = Set.of();

	@PreRemove
	private void removeEducationFromUsersProfile() {
		discipline.getExams().remove(this);
	}
}
