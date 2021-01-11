package com.vlad.app.jsonconverters;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.vlad.app.model.Discipline;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DisciplineSetJSONConverter extends StdConverter<Set<Discipline>, Set<String>> {

	@Override public Set<String> convert(Set<Discipline> value) {
		return value.stream().map(Discipline::getName).collect(Collectors.toSet());
	}
}
