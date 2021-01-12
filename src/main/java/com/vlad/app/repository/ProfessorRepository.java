package com.vlad.app.repository;

import com.vlad.app.model.Professor;
import com.vlad.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor,Long> {
	Optional<Professor> findByCNP(String CNP);

	List<Professor> findAllByCNPStartingWith(String CNP);

	Optional<Professor> findByUser(User user);
}
