package com.vlad.app.csvconverters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.vlad.app.exception.ResourceNotFoundException;
import com.vlad.app.model.Professor;
import com.vlad.app.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfessorConverter extends AbstractBeanField<Professor, String> {

	@Autowired
	private ProfessorRepository professorRepository;

	public ProfessorConverter() { }

	@Override protected Object convert(String val)
			throws CsvDataTypeMismatchException, ResourceNotFoundException {
		if (val.length() != 13)
			throw new CsvDataTypeMismatchException("CNP length must be 13");
		return professorRepository.findByCNP(val)
				.orElseThrow(() -> new ResourceNotFoundException(Professor.class.getName(), "ID", val));
	}
}
