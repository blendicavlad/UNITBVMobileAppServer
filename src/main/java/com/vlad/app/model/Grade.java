package com.vlad.app.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vlad.app.deserializers.ExamDeserializer;
import com.vlad.app.deserializers.StudentDeserializer;
import com.vlad.app.jsonconverters.ExamJSONConverter;
import com.vlad.app.jsonconverters.StudentJSONConverter;
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
import javax.persistence.PreRemove;
import javax.persistence.Table;

@Entity
@Table(name = "grades")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Grade extends Auditable<User> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 2)
	private int mark;

	@ManyToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "exam_id", referencedColumnName = "id")
	@JsonDeserialize(using = ExamDeserializer.class)
	@JsonSerialize(converter = ExamJSONConverter.class)
	private Exam exam;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="student_id", referencedColumnName = "id", nullable = false)
	@JsonDeserialize(using = StudentDeserializer.class)
	@JsonSerialize(converter = StudentJSONConverter.class)
	private Student student;


	@PreRemove
	private void removeEducationFromUsersProfile() {
		exam.getGrades().remove(this);
		student.getGrades().remove(this);
	}
}
