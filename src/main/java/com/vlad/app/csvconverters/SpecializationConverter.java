package com.vlad.app.csvconverters;

import com.opencsv.bean.AbstractBeanField;
import com.vlad.app.exception.ResourceNotFoundException;
import com.vlad.app.model.Specialization;
import com.vlad.app.repository.SpecializationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpecializationConverter extends AbstractBeanField<Specialization, String> {

	@Autowired
	private SpecializationRepository specializationRepository;

	public SpecializationConverter() { }

	@Override protected Object convert(String val) throws ResourceNotFoundException {
		return specializationRepository.findById(val)
				.orElseThrow(() -> new ResourceNotFoundException(Specialization.class.getName(), "ID", val));
	}
}
