package com.vlad.app.controller;

import com.vlad.app.model.Class;
import com.vlad.app.repository.ClassRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("classes")
@PreAuthorize("hasRole('ADMIN')")
public class ClassController {

	private final ClassRepository classRepository;

	public ClassController(ClassRepository classRepository) {
		this.classRepository = classRepository;
	}


	@GetMapping("list")
	public List<Class> list(@RequestParam(required = false) String specialization_id) {
		if (specialization_id != null)
			return classRepository.findAllBySpecializationId(specialization_id);
		else
			return classRepository.findAll();
	}
}
