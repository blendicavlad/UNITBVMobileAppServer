package com.vlad.app.controller;

import com.vlad.app.model.Discipline;
import com.vlad.app.model.UserType;
import com.vlad.app.repository.DisciplineRepository;
import com.vlad.app.repository.StudentRepository;
import com.vlad.app.security.SecurityContextProvider;
import com.vlad.app.utils.QueryBuilder;
import io.swagger.annotations.ApiOperation;
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
@RequestMapping("disciplines")
public class DisciplineController {

	final private DisciplineRepository disciplineRepository;
	final private StudentRepository studentRepository;
	final private EntityManager entityManager;
	final private SecurityContextProvider securityContextProvider;

	public DisciplineController(DisciplineRepository disciplineRepository,
			StudentRepository studentRepository, EntityManager entityManager,
			SecurityContextProvider securityContextProvider) {
		this.disciplineRepository = disciplineRepository;
		this.studentRepository = studentRepository;
		this.entityManager = entityManager;
		this.securityContextProvider = securityContextProvider;
	}

	@ApiOperation(
			value = "list departments",
			notes = "list all departments"
	)
	@GetMapping("list")
	public List<Discipline> listDisciplines(
			@RequestParam(required = false) String name,
			@RequestParam(required = false) Long professor_id,
			@RequestParam(required = false) String specialization_id) {

		var query = "SELECT discipline from Discipline discipline";
		Map<String, Object> params = new LinkedHashMap<>();
		List<String> predicates = new ArrayList<>();
		if (name != null) {
			predicates.add("discipline.name = :discipline_name");
			params.put("discipline_name", name);
		}
		if (professor_id != null) {
			predicates.add("discipline.professor.id = :prof_id");
			params.put("prof_id", professor_id);
		}
		if (specialization_id != null) {
			predicates.add("discipline.specialization.id = :spec_id");
			params.put("spec_id", specialization_id);
		}
		if (securityContextProvider.getCurrentContextUser().getUserType().equals(UserType.PROFESSOR)) {
			predicates.add("discipline.professor.user.id = :secure_prof_id");
			params.put("secure_prof_id", securityContextProvider.getCurrentContextUser().getId());
		} else if (securityContextProvider.getCurrentContextUser().getUserType().equals(UserType.STUDENT)) {
			predicates.add("discipline IN (SELECT student.group.clss.specialization.disciplines " +
					"from Student student where student.user.id=:secure_stud_id)");
			params.put("stud_id", securityContextProvider.getCurrentContextUser().getId());
		}
		return new QueryBuilder<Discipline>()
				.buildTypedQuery(query, params, predicates, entityManager, Discipline.class)
				.getResultList();
	}
}
