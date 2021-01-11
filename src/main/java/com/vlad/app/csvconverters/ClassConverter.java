package com.vlad.app.csvconverters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.vlad.app.exception.ResourceNotFoundException;
import com.vlad.app.model.Class;
import com.vlad.app.repository.ClassRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClassConverter extends AbstractBeanField<Class, String> {

	@Autowired
	private ClassRepository classRepository;

	public ClassConverter() { }

	@Override protected Object convert(String val)
			throws  CsvConstraintViolationException, ResourceNotFoundException {
		if (StringUtils.isEmpty(val))
			throw new CsvConstraintViolationException("Class ID value must not be empty");
		return classRepository.findById(val)
				.orElseThrow(() -> new ResourceNotFoundException(Class.class.getName(), "ID", val));
	}
}