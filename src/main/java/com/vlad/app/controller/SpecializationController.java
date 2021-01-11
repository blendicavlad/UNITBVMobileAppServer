package com.vlad.app.controller;

import com.vlad.app.model.Specialization;
import com.vlad.app.repository.SpecializationRepository;
import com.vlad.app.utils.QueryBuilder;
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
@RequestMapping("specializations")
public class SpecializationController {

	private final SpecializationRepository specializationRepository;
	private final EntityManager entityManager;


	public SpecializationController(SpecializationRepository specializationRepository, EntityManager entityManager) {
		this.specializationRepository = specializationRepository;
		this.entityManager = entityManager;
	}

	@GetMapping("list")
	private List<Specialization> list(
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String department_name,
			@RequestParam(required = false) String department_id
	) {
		var query = "SELECT specialization from Specialization specialization";
		Map<String, Object> params = new LinkedHashMap<>();
		List<String> predicates = new ArrayList<>();
		if (name != null) {
			predicates.add("specialization.name = :spec_name");
			params.put("spec_name", name);
		}
		if (department_name != null) {
			predicates.add("specialization.department.name = :dep_name");
			params.put("dep_name", department_name);
		}
		if (department_id != null) {
			predicates.add("specialization.department.id = :dep_id");
			params.put("dep_id", department_id);
		}
		return new QueryBuilder<Specialization>()
				.buildTypedQuery(query, params, predicates, entityManager, Specialization.class)
				.getResultList();
	}
}
