package com.egui.gabo.webflux.app.controller;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;

import com.egui.gabo.webflux.app.models.document.Product;
import com.egui.gabo.webflux.app.service.ProductService;

import reactor.core.publisher.Flux;

/**
 * Thymeleaf controller for product views.
 * Demonstrates various reactive streaming patterns with Thymeleaf templates.
 * 
 * @author Gabriel Eguiguren P.
 */
@Controller
public class ProductController {
	
	private static final Logger log = LoggerFactory.getLogger(ProductController.class);	
	
	@Autowired
	private ProductService productService;
	
	/**
	 * Standard product listing with uppercase names.
	 * Uses default Thymeleaf subscription to Flux.
	 */
	@GetMapping("/")
	public String listarProductos(Model model) {
		model.addAttribute("title", "Product List");
		
		Flux<Product> products = productService.findAll()
				.map(product -> {
					product.setName(product.getName().toUpperCase());
					return product;
				});
		
		products.subscribe(prod -> log.info(prod.getName()));
		model.addAttribute("products", products);
		
		return "listProducts";
	} 
	
	/**
	 * Large dataset demonstration (500x repeated).
	 * Tests performance with large reactive streams.
	 */
	@GetMapping("/list-huge")
	public String listarProductosFull(Model model) {
		model.addAttribute("title", "Product List");
		
		Flux<Product> products = productService.findAll()
				.map(product -> {
					product.setName(product.getName().toUpperCase());
					return product;
				})
				.repeat(500);
		
		model.addAttribute("products", products);
		return "listProducts";
	}
	
	/**
	 * Chunked response with backpressure.
	 * Uses max-chunk-size=1024 defined in application.properties.
	 */
	@GetMapping("/list-chunked")
	public String listarProductsChunked(Model model) {
		model.addAttribute("title", "Product List");
		
		Flux<Product> products = productService.findAll()
				.map(product -> {
					product.setName(product.getName().toUpperCase());
					return product;
				})
				.repeat(500);
		
		model.addAttribute("products", products);
		return "list-chunked";
	} 
	
	/**
	 * Reactive Data Driver with controlled data flow.
	 * Sends data in chunks of 2 with 1-second delays between elements.
	 */
	@GetMapping("/list")
	public String listarReactiveDataDriver(Model model) {
		model.addAttribute("title", "Product List");
		
		Flux<Product> products = productService.findAll()
				.map(product -> {
					product.setName(product.getName().toUpperCase());
					return product;
				})
				.delayElements(Duration.ofSeconds(1));
		
		model.addAttribute("products", new ReactiveDataDriverContextVariable(products, 2));
		return "listProducts";
	} 
}
