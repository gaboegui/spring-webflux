package com.egui.gabo.webflux.app.models.document;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Product entity/document for MongoDB.
 * Represents a product with name, price, and creation timestamp.
 * 
 * @author Gabriel Eguiguren P.
 */
@Document(collection = "products")
public class Product {
	
	@Id
	private String id;
	
	private String name;
	
	private Double price;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createAt;
	
	/** Default constructor. */
	public Product() {
	}

	/**
	 * Constructor with name and price.
	 * 
	 * @param name Product name
	 * @param price Product price
	 */
	public Product(String name, Double price) {
		this.name = name;
		this.price = price;
	}

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

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
}
