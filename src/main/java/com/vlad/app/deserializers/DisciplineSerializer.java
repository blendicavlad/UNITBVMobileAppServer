package com.vlad.app.deserializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.vlad.app.model.Discipline;

import java.io.IOException;

public class DisciplineSerializer extends StdSerializer<Discipline> {

	public DisciplineSerializer() {
		this(null);
	}

	public DisciplineSerializer(Class<Discipline> t) {
		super(t);
	}

	@Override public void serialize(Discipline value, JsonGenerator gen, SerializerProvider provider)
			throws IOException {

		gen.writeStartObject();
		gen.writeStringField("id", value.getId());
		gen.writeStringField("name", value.getName());
		gen.writeStringField("description", value.getDescription());
		gen.writeStringField("professor", value.getProfessor().getUser().getFullName());
		gen.writeStringField("specialization", value.getSpecialization().getName());
		gen.writeEndObject();
	}
}
