package com.egui.gabo.webflux.app.service;

import com.egui.gabo.webflux.app.models.document.Category;
import com.egui.gabo.webflux.app.models.document.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
	
	public Flux<Product> findAll();	
	
	public Flux<Product> findAllNameUppercase();
	
	public Mono<Product> findById(String id);
	
	public Mono<Product> save(Product p);
	
	public Mono<Void> delete(Product p);
	
	
	/* Simplify calls in Controller*/
	public Flux<Category> findAllCategories();
	
	public Mono<Category> findCategoryById(String id);
	
	public Mono<Category> saveCategory(Category c);

}
