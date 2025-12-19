package com.egui.gabo.webflux.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;	
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.egui.gabo.webflux.app.models.document.Product;
import com.egui.gabo.webflux.app.models.repository.ProductRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST controller for product API endpoints.
 * Demonstrates reactive REST API patterns with Spring WebFlux.
 * 
 * @author Gabriel Eguiguren P.
 */
@RestController
@RequestMapping("/api/products")
public class ProductRestController {
	
	private static final Logger log = LoggerFactory.getLogger(ProductRestController.class);	
	
	@Autowired
	private ProductRepository productDao;
	
	/**
	 * Get all products with names converted to uppercase.
	 * Returns a Flux stream of products.
	 */
	@GetMapping
	public Flux<Product> listarProductos() {
		Flux<Product> products = productDao.findAll()
				.map(product -> {
					product.setName(product.getName().toUpperCase());
					return product;
				})
				.doOnNext(prod -> log.info(prod.getName()));
		
		return products;
	} 
	
	/**
	 * Get a single product by ID.
	 * Returns a Mono containing the product or empty if not found.
	 * 
	 * @param id Product ID
	 * @return Mono containing the product
	 */
	@GetMapping("/{id}")
	public Mono<Product> listarProducto(@PathVariable String id) {
		Flux<Product> productos = productDao.findAll();
		Mono<Product> founded = productos
				.filter(prod -> prod.getId().equals(id))
				.next();
		
		return founded;
	}
	
}
