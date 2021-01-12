package com.vlad.app.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.vlad.app.exception.ResourceNotFoundException;
import com.vlad.app.model.Exam;
import com.vlad.app.repository.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class ExamDeserializer extends StdDeserializer<Exam> {

	@Autowired
	private ExamRepository examRepository;

	public ExamDeserializer() {
		this(null);
	}

	public ExamDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override public Exam deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		Long id = p.getCodec().readValue(p, Long.class);
//		Long id = node.get("id").longValue();
		return examRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Exam", "ID", id));
	}
}
