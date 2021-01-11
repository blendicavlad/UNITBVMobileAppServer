package com.vlad.app.jsonconverters;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.vlad.app.model.Group;

public class GroupJSONConverter extends StdConverter<Group, String> {
	@Override public String convert(Group value) {
		return value.getId();
	}
}
