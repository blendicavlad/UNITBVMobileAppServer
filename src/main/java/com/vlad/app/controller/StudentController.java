package com.vlad.app.controller;

import com.vlad.app.model.Group;
import com.vlad.app.model.Student;
import com.vlad.app.model.UserType;
import com.vlad.app.repository.ProfessorRepository;
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
	private final ProfessorRepository professorRepository;
	private final EntityManager entityManager;
	private final SecurityContextProvider securityContextProvider;

	public StudentController(StudentRepository studentRepository,
			ProfessorRepository professorRepository, EntityManager entityManager,
			SecurityContextProvider securityContextProvider) {
		this.studentRepository = studentRepository;
		this.professorRepository = professorRepository;
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
			predicates.add("student.group IN :prof_groups");
			var native_query = "select * from studentgroups\n" +
					"join classes c on studentgroups.class_id = c.id\n" +
					"join specializations s on s.id = c.specialization_id\n" +
					"join disciplines d on s.id = d.specialization_id\n" +
					"join professors p on d.professors_id = p.id\n" +
					"join users u on p.user_id = u.id\n" +
					"where u.id = ?1";
			params.put("prof_groups", entityManager.createNativeQuery(native_query, Group.class)
					.setParameter(1, securityContextProvider.getCurrentContextUser().getId())
					.getResultList());
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
