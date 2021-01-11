package com.vlad.app.repository;

import com.vlad.app.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {

	List<Exam> findAllByDisciplineId(String discipline_id);
}
