package io.contextr.model;

import org.springframework.data.annotation.Id;

public class PersistModel {
	
	@Id
    private String id;
	
	String profile;
	String text;

	public PersistModel(String label, String text) {
		this.profile = label;
		this.text = text;
	}
	
	public PersistModel() {
	}

	public String getLabel() {
		return profile;
	}

	public String getText() {
		return text;
	}

}
