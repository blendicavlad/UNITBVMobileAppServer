package com.vlad.app.csvconverters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.vlad.app.exception.ResourceNotFoundException;
import com.vlad.app.model.Group;
import com.vlad.app.repository.GroupRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupConverter extends AbstractBeanField<Group, String> {

	@Autowired
	private GroupRepository groupRepository;

	public GroupConverter() {
	}

	@Override protected Object convert(String val)
			throws CsvConstraintViolationException, ResourceNotFoundException {
		if (StringUtils.isEmpty(val))
			throw new CsvConstraintViolationException("Group ID value must not be empty");
		return groupRepository.findById(val)
				.orElseThrow(() -> new ResourceNotFoundException(Group.class.getName(), "ID", val));
	}
}
