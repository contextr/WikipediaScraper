package io.contextr.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.contextr.serializer.WikipediaJSONModelDeserializer;

@JsonDeserialize(using = WikipediaJSONModelDeserializer.class)
public class WikipediaJSONModel {
	String text;

	public WikipediaJSONModel(String text) {
		this.text = text;
	}
	
	public WikipediaJSONModel() {
	}

	public String getText() {
		return text;
	}

}
