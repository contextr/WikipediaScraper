package io.contextr.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import io.contextr.model.WikipediaJSONModel;

public class WikipediaJSONModelDeserializer extends JsonDeserializer<WikipediaJSONModel> {

	private String wikipediaTextTag = "extract";

	@Override
	public WikipediaJSONModel deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		String text = "";

		while (!jp.isClosed()) {
			JsonToken jsonToken = jp.nextToken();

			if (JsonToken.FIELD_NAME.equals(jsonToken)) {
				String fieldName = jp.getCurrentName();

				jsonToken = jp.nextToken();

				if (fieldName.equals(wikipediaTextTag)) {
					text = jp.getValueAsString();
				}
			}
		}
		return new WikipediaJSONModel(text);
	}
}
