package com.barrans.master;

import javax.inject.Singleton;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.jackson.ObjectMapperCustomizer;

@Singleton
public class ApplicationObjectMapperCustomizer implements ObjectMapperCustomizer{

	@Override
	public void customize(ObjectMapper objectMapper) {
		objectMapper.configure(Feature.ALLOW_COMMENTS, true);	
		// To suppress serializing properties with null values
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}
	
}
