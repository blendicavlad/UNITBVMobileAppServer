package com.vlad.app.jsonconverters;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.vlad.app.model.User;

public class UserJSONConverter extends StdConverter<User, String> {

	@Override public String convert(User value) {
		return value.getFullName();
	}
}
