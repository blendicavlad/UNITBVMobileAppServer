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
		if(securityContextProvider.getCurrentContextUser().getUserType().equals(UserType.PROFESSOR)) {
			if (discipline_id != null)
				return examRepository.findAllByDisciplineId(discipline_id);
			else
				return examRepository.findAll();
		} else {
			var student = studentRepository.findByUser(securityContextProvider.getCurrentContextUser());
			if (student.isEmpty())
				throw new RuntimeException("Could not determine student from request");
			else {
				var query = "SELECT exam FROM Exam exam " ;
				Map<String, Object> params = new LinkedHashMap<>();
				List<String> predicates = new ArrayList<>();

				predicates.add(" exam.id IN " +
						" (SELECT grade.exam.id FROM Grade grade " +
						"  WHERE grade.student.id = :student_id) ");
				params.put("student_id", student.get().getId());

				if (discipline_id != null) {
					predicates.add(" exam.discipline.id = :disc_id");
					params.put("disc_id", discipline_id);
				}

				return new QueryBuilder<Exam>()
						.buildTypedQuery(query, params, predicates, entityManager, Exam.class)
						.getResultList();
			}
		}
	}
}
