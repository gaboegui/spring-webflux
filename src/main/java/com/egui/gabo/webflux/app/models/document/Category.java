package com.egui.gabo.webflux.app.models.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotEmpty;

@Document(collection = "categories")
public class Category {
	
	@Id
	@NotEmpty
	private String id;
	
	@NotEmpty
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Category(@NotEmpty String name) {
		this.name = name;
	}

	public Category() {
	}

}
