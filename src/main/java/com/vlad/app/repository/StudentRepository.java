package com.vlad.app.repository;

import com.vlad.app.model.Student;
import com.vlad.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student,Long> {

	Optional<Student> findByCNP(String CNP);

	List<Student> findAllByCNPStartingWith(String cnp);

	Optional<Student> findByUser(User user);
}
