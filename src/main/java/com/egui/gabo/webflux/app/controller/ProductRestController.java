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

@RestController
@RequestMapping("/api/products")
public class ProductRestController {
	
	private static final Logger log = LoggerFactory.getLogger(ProductRestController.class);	
	
	@Autowired
	private ProductRepository productDao;
	
	@GetMapping
	public Flux<Product> listarProductos() {
		// Flux<Product> productos = productDao.findAll();
		Flux<Product> products = productDao.findAll()
				.map(product-> {
					product.setName(product.getName().toUpperCase());
					return product;
				})
				.doOnNext(prod -> log.info(prod.getName()));
		
		return products;  // must match with templates/html file name
	} 
	
	@GetMapping("/{id}")
	public Mono<Product> listarProducto(@PathVariable String id) {
		
		//return productDao.findById(id);
		
		Flux<Product> productos = productDao.findAll();
		Mono<Product> founded = productos
				.filter(prod -> prod.getId().equals(id))
				.next();	// return the first	
		
		return founded;
	}
	
}
