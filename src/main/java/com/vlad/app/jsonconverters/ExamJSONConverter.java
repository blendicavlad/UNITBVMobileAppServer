package com.vlad.app.jsonconverters;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.vlad.app.model.Exam;

public class ExamJSONConverter extends StdConverter<Exam, String> {
	@Override public String convert(Exam value) {
		return value.getId() + " " + value.getDiscipline().getName() + " " + value.getExamDate();
	}
}
