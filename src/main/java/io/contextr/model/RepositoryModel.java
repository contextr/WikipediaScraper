package io.contextr.model;

import org.springframework.data.annotation.Id;

public class RepositoryModel {
	
	@Id
    private String id;
	
	String profile;
	String text;

	public RepositoryModel(String label, String text) {
		this.profile = label;
		this.text = text;
	}
	
	public RepositoryModel() {
	}

	public String getLabel() {
		return profile;
	}

	public String getText() {
		return text;
	}

}
