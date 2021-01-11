package com.vlad.app.controller;

import com.vlad.app.model.Professor;
import com.vlad.app.repository.ProfessorRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("professors")
@PreAuthorize("hasRole('ADMIN')")
public class ProfessorController {

	private final ProfessorRepository professorRepository;

	public ProfessorController(ProfessorRepository professorRepository) {
		this.professorRepository = professorRepository;
	}

	@GetMapping("list")
	public List<Professor> list(@RequestParam(required = false) String cnp) {
		if (cnp != null)
			return professorRepository.findAllByCNPStartingWith(cnp);
		else
			return professorRepository.findAll();
	}
}
