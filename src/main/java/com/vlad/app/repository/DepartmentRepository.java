package com.vlad.app.repository;

import com.vlad.app.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, String> {

	public List<Department> findByNameStartingWith(String val);
}
