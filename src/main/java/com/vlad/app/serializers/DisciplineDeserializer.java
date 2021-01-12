package com.vlad.app.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.vlad.app.exception.ResourceNotFoundException;
import com.vlad.app.model.Discipline;
import com.vlad.app.repository.DisciplineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class
DisciplineDeserializer extends StdDeserializer<Discipline> {

	@Autowired
	private DisciplineRepository disciplineRepository;

	public DisciplineDeserializer() {
		this(null);
	}

	public DisciplineDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override public Discipline deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException, ResourceNotFoundException {
//		JsonNode node = p.getCodec().readTree(p);
//		String id = node.get("id").asText();
		String id = p.getCodec().readValue(p, String.class);
		return disciplineRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Discipline", "ID", id));
	}
}
