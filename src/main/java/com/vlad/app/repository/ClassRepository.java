package com.vlad.app.repository;

import com.vlad.app.model.Class;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassRepository extends JpaRepository<Class, String> {

	public List<Class> findAllBySpecializationId(String ID);
}
