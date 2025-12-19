package com.egui.gabo.webflux.app.models.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.egui.gabo.webflux.app.models.document.Product;

/**
 * Reactive MongoDB repository for Product entities.
 * Provides reactive CRUD operations for Product documents.
 * 
 * @author Gabriel Eguiguren P.
 */
public interface ProductRepository extends ReactiveMongoRepository<Product, String> {

}
