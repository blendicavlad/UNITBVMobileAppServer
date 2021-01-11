package com.vlad.app.repository;

import com.vlad.app.model.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisciplineRepository extends JpaRepository<Discipline, String> {
}
