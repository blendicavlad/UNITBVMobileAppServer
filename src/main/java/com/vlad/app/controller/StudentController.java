package com.vlad.app.controller;

import com.vlad.app.model.Student;
import com.vlad.app.repository.StudentRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("students")
@PreAuthorize("hasRole('ADMIN')")
public class StudentController {

	private final StudentRepository studentRepository;

	public StudentController(StudentRepository studentRepository) {
		this.studentRepository = studentRepository;
	}

	@GetMapping("list")
	public List<Student> list(@RequestParam(required = false) String cnp) {
		if (cnp != null)
			return studentRepository.findAllByCNPStartingWith(cnp);
		else
			return studentRepository.findAll();
	}

}
