package com.vlad.app.controller;

import com.vlad.app.model.Student;
import com.vlad.app.model.UserType;
import com.vlad.app.repository.StudentRepository;
import com.vlad.app.security.SecurityContextProvider;
import com.vlad.app.utils.QueryBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("students")
public class StudentController {

	private final StudentRepository studentRepository;
	private final EntityManager entityManager;
	private final SecurityContextProvider securityContextProvider;

	public StudentController(StudentRepository studentRepository, EntityManager entityManager,
			SecurityContextProvider securityContextProvider) {
		this.studentRepository = studentRepository;
		this.entityManager = entityManager;
		this.securityContextProvider = securityContextProvider;
	}

	@PreAuthorize("hasAnyRole('PROFESSOR','ADMIN')")
	@GetMapping("list")
	public List<Student> list(@RequestParam(required = false) String cnp) {
		if (securityContextProvider.getCurrentContextUser().getUserType().equals(UserType.PROFESSOR)) {
			var query = "SELECT student from Student student";
			Map<String, Object> params = new LinkedHashMap<>();
			List<String> predicates = new ArrayList<>();
			predicates.add("student.group.id IN (SELECT group.id from Group group " +
					"where group.clss.specialization.disciplines in " +
					"(SELECT professor.disciplines from Professor professor" +
					" where professor.user.id = :secure_prof_id))");
			params.put("secure_user_id", securityContextProvider.getCurrentContextUser().getId());
			if (cnp != null) {
				predicates.add("student.cnp=:cnp");
				params.put("cnp", cnp);
			}
			return new QueryBuilder<Student>()
					.buildTypedQuery(query, params, predicates, entityManager, Student.class)
					.getResultList();
		} else {
			if (cnp != null)
				return studentRepository.findAllByCNPStartingWith(cnp);
			else
				return studentRepository.findAll();
		}
	}

}
