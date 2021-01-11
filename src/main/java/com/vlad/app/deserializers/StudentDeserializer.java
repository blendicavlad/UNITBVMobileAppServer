package com.vlad.app.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.vlad.app.exception.ResourceNotFoundException;
import com.vlad.app.model.Student;
import com.vlad.app.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class StudentDeserializer extends StdDeserializer<Student> {

	@Autowired
	private StudentRepository studentRepository;

	public StudentDeserializer() {
		this(null);
	}

	public StudentDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override public Student deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		Long id = p.getCodec().readValue(p, Long.class);
		return studentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Student", "ID", id));
	}
}
