package com.vlad.app.jsonconverters;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.vlad.app.model.Department;

public class DepartmentJSONConverter extends StdConverter<Department, String> {

	@Override public String convert(Department value) {
		return value.getName();
	}
}
