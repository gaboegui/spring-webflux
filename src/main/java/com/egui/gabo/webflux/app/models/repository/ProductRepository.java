package com.egui.gabo.webflux.app.models.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.egui.gabo.webflux.app.models.document.Product;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {

}
