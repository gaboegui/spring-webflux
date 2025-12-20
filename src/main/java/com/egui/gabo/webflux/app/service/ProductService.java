package com.egui.gabo.webflux.app.service;

import com.egui.gabo.webflux.app.models.document.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
	
	public Flux<Product> findAll();	
	
	public Flux<Product> findAllNameUppercase();
	
	public Mono<Product> findById(String id);
	
	public Mono<Product> save(Product p);
	
	public Mono<Void> delete(Product p);



}
