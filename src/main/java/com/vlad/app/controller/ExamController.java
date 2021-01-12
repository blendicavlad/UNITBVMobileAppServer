package com.vlad.app.controller;

import com.vlad.app.model.Exam;
import com.vlad.app.model.UserType;
import com.vlad.app.payload.ApiResponse;
import com.vlad.app.repository.ExamRepository;
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
@RequestMapping("/exams")
public class ExamController {

	private final ExamRepository examRepository;
	private final SecurityContextProvider securityContextProvider;
	private final StudentRepository studentRepository;
	private final EntityManager entityManager;

	public ExamController(ExamRepository examRepository,
			SecurityContextProvider securityContextProvider,
			StudentRepository studentRepository, EntityManager entityManager) {
		this.examRepository = examRepository;
		this.securityContextProvider = securityContextProvider;
		this.studentRepository = studentRepository;
		this.entityManager = entityManager;
	}

	@PreAuthorize("hasRole('PROFESSOR')")
	@PostMapping(value = "/create", consumes = { "application/json" })
	public Exam create(@RequestBody Exam exam) {
		if (exam.getExamDate() == null) {
			throw new RuntimeException("Date cannot be null");
		}
		if (exam.getExamDate().getTime() < System.currentTimeMillis()) {
			throw new RuntimeException("Date cannot be lower than now");
		}
		if (exam.getId() != null) {
			var examOptional = examRepository.findById(exam.getId());
			if (examOptional.isPresent()) {
				var x = examOptional.get();
				x.setExamDate(exam.getExamDate());
				return examRepository.save(x);
			} else {
				throw new RuntimeException("Exam with id: " + exam.getId() + " not found");
			}
		}
		return examRepository.save(exam);
	}

	@PreAuthorize("hasRole('PROFESSOR')")
	@PostMapping(value = "/delete")
	public ResponseEntity<ApiResponse> delete(@RequestParam Long exam_id) {
		var examOpt = examRepository.findById(exam_id);
		if (examOpt.isEmpty())
			return ResponseEntity.ok(new ApiResponse(false, "Exam with id: " + exam_id + " not found"));
		else {
			examRepository.delete(examOpt.get());
			return ResponseEntity.ok(new ApiResponse(true, "Exam with id: " + exam_id + " was deleted"));
		}
	}

	@PreAuthorize("hasAnyRole('PROFESSOR','STUDENT')")
	@GetMapping(value = "/list")
	public List<Exam> list(@RequestParam(required = false) String discipline_id) {
		var query = "SELECT exam FROM Exam exam ";
		Map<String, Object> params = new LinkedHashMap<>();
		List<String> predicates = new ArrayList<>();
		if (securityContextProvider.getCurrentContextUser().getUserType().equals(UserType.PROFESSOR)) {
			predicates.add("exam.discipline.professor.user.id = :secure_user_id");
			params.put("secure_user_id", securityContextProvider.getCurrentContextUser().getId());
			if (discipline_id != null) {
				predicates.add("exam.discipline.id=:disc_id");
				params.put("disc_id", discipline_id);
			}
		} else {
			predicates.add(" exam.id IN " +
					" (SELECT grade.exam.id FROM Grade grade " +
					"  WHERE grade.student.user.id = :secure_user_id) ");
			params.put("secure_user_id", securityContextProvider.getCurrentContextUser().getId());
			if (discipline_id != null) {
				predicates.add(" exam.discipline.id = :disc_id");
				params.put("disc_id", discipline_id);
			}
		}
		return new QueryBuilder<Exam>()
				.buildTypedQuery(query, params, predicates, entityManager, Exam.class)
				.getResultList();
	}
}
