package com.egui.gabo.webflux.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.egui.gabo.webflux.app.models.document.Product;
import com.egui.gabo.webflux.app.service.ProductService;

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
	private ProductService productService;

	/**
	 * Get all products with names converted to uppercase.
	 * Returns a Flux stream of products.
	 * 
	 * @return a Flux of all products
	 */
	@GetMapping
	public Flux<Product> listarProductos() {
		Flux<Product> products = productService.findAll()
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

		// Mono<Product> founded = productDao.findById(id);

		Flux<Product> productos = productService.findAllNameUppercase();
		Mono<Product> founded = productos
				.filter(prod -> prod.getId().equals(id))
				.next();

		return founded;
	}

}
