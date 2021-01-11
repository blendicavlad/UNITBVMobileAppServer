package com.vlad.app.jsonconverters;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.vlad.app.model.Student;

public class StudentJSONConverter extends StdConverter<Student, String> {
	@Override public String convert(Student value) {
		return value.getUser().getFullName();
	}
}
