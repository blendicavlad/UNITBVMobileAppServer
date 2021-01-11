package com.vlad.app.jsonconverters;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.vlad.app.model.Specialization;

public class SpecializationJSONConverter extends StdConverter<Specialization, String> {

	@Override public String convert(Specialization value) {
		return value.getName();
	}
}
