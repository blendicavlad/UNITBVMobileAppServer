package com.vlad.app.controller;

import com.vlad.app.model.Exam;
import com.vlad.app.model.Grade;
import com.vlad.app.model.UserType;
import com.vlad.app.payload.ApiResponse;
import com.vlad.app.repository.GradeRepository;
import com.vlad.app.repository.StudentRepository;
import com.vlad.app.security.SecurityContextProvider;
import com.vlad.app.utils.QueryBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("grades")
public class GradeController {

	private final GradeRepository gradeRepository;
	private final SecurityContextProvider securityContextProvider;
	private final StudentRepository studentRepository;
	private final EntityManager entityManager;

	public GradeController(GradeRepository gradeRepository,
			SecurityContextProvider securityContextProvider,
			StudentRepository studentRepository, EntityManager entityManager) {
		this.gradeRepository = gradeRepository;
		this.securityContextProvider = securityContextProvider;
		this.studentRepository = studentRepository;
		this.entityManager = entityManager;
	}

	@PreAuthorize("hasRole('PROFESSOR')")
	@PostMapping(value = "/create", consumes = { "application/json" })
	public Grade create(@RequestBody Grade grade) {
		return gradeRepository.save(grade);
	}

	@PreAuthorize("hasRole('PROFESSOR')")
	@PostMapping(value = "/delete")
	public ResponseEntity<ApiResponse> delete(@RequestParam Long grade_id) {
		var gradeOpt = gradeRepository.findById(grade_id);
		if (gradeOpt.isEmpty())
			return ResponseEntity.ok(new ApiResponse(false, "Grade with id: " + grade_id + " not found"));
		else {
			gradeRepository.delete(gradeOpt.get());
			return ResponseEntity.ok(new ApiResponse(true, "Grade with id: " + grade_id + " was deleted"));
		}
	}

	@PreAuthorize("hasAnyRole('PROFESSOR','STUDENT')")
	@GetMapping(value = "/list")
	public List<Grade> list(@RequestParam(required = false) String discipline_id,
			@RequestParam(required = false) Long exam_id, @RequestParam(required = false) Long student_id) {
		var query = "SELECT grade FROM Grade grade ";
		Map<String, Object> params = new LinkedHashMap<>();
		List<String> predicates = new ArrayList<>();
		if (securityContextProvider.getCurrentContextUser().getUserType().equals(UserType.PROFESSOR)) {
			if (student_id != null) {
				predicates.add("grade.student.id = :student_id");
				params.put("student_id", student_id);
			}
		} else {
			var student = studentRepository.findByUser(securityContextProvider.getCurrentContextUser());
			if (student.isEmpty())
				throw new RuntimeException("Could not determine student from request");
			else {
				predicates.add("grade.student.id = :student_id");
				params.put("student_id", student.get().getId());
			}
		}
		if (exam_id != null) {
			predicates.add("grade.exam.id = :exam_id");
			params.put("exam_id",exam_id);
		}
		if (discipline_id != null) {
			predicates.add(" grade.exam.discipline.id = :disc_id");
			params.put("disc_id", discipline_id);
		}
		return new QueryBuilder<Grade>()
				.buildTypedQuery(query, params, predicates, entityManager, Grade.class)
				.getResultList();
	}
}
