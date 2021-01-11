package com.vlad.app.csvconverters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.vlad.app.exception.ResourceNotFoundException;
import com.vlad.app.model.Department;
import com.vlad.app.repository.DepartmentRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DepartmentConverter extends AbstractBeanField<Department, String> {

	@Autowired
	private DepartmentRepository departmentRepository;

	public DepartmentConverter() {

	}

	@Override protected Object convert(String val) throws CsvConstraintViolationException, ResourceNotFoundException {
		if (StringUtils.isEmpty(val))
			throw new CsvConstraintViolationException("Department ID value must not be empty");
		return departmentRepository.findById(val)
				.orElseThrow(() -> new ResourceNotFoundException(Department.class.getSimpleName(), "ID", val));
	}
}
