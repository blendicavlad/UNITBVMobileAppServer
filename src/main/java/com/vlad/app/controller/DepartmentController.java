package com.vlad.app.controller;

import com.vlad.app.model.Department;
import com.vlad.app.repository.DepartmentRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/departments")
@PreAuthorize("hasRole('ADMIN')")
public class DepartmentController {

	private DepartmentRepository departmentRepository;

	public DepartmentController(DepartmentRepository departmentRepository) {
		this.departmentRepository = departmentRepository;
	}

	@GetMapping("list")
	public List<Department> listDepartments(@RequestParam(required = false) String name) {
		if (name != null) {
			return departmentRepository.findByNameStartingWith(name);
		} else {
			return departmentRepository.findAll();
		}
	}
}
