package com.egui.gabo.webflux.app.controller;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;

import com.egui.gabo.webflux.app.models.document.Product;
import com.egui.gabo.webflux.app.service.ProductService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Thymeleaf controller for product views.
 * Demonstrates various reactive streaming patterns with Thymeleaf templates.
 * 
 * SessionAttributes avoid the use of hidden input to indentify save or update action
 * 
 * @author Gabriel Eguiguren P.
 */
@SessionAttributes("product")
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
	public Mono<String> listarProductos(Model model) {
		model.addAttribute("title", "Product List");
		
		Flux<Product> products = productService.findAllNameUppercase();
				
		
		products.subscribe(prod -> log.info(prod.getName()));
		model.addAttribute("products", products);
		
		return Mono.just("listProducts");  // must match name of html file in resources/static
	} 
	
	/**
	 * Large dataset demonstration (500x repeated).
	 * Tests performance with large reactive streams.
	 */
	@GetMapping("/list-huge")
	public Mono<String> listarProductosFull(Model model) {
		model.addAttribute("title", "Product List");
		
		Flux<Product> products = productService.findAllNameUppercase()
				.repeat(500);
		
		model.addAttribute("products", products);
		return Mono.just("listProducts");
	}
	
	@GetMapping("/product-form")
	public Mono<String> createForm(Model model) {

		model.addAttribute("title", "Product Form");
		model.addAttribute("buttonText", "Create");
		model.addAttribute("product", new Product());  	// must match @SessionAttributes
		return Mono.just("productForm");		
	}

	@GetMapping("/product-form/{id}")
	public Mono<String> updateForm(@PathVariable String id, Model model) {
		
		Mono<Product> productDb = productService.findById(id)
				.defaultIfEmpty(new Product());		// avoids error message if not found
		
		model.addAttribute("title", "Product Edit");
		model.addAttribute("buttonText", "Edit");
		model.addAttribute("product",  productDb); 		// must match @SessionAttributes
		
		return Mono.just("productForm");
	}

	
	@PostMapping("/product-form")
	public Mono<String> saveForm(Product product, SessionStatus session) {
		
		session.setComplete();  			// destroys the object from @SessionAttributes
		
		return productService.save(product)
				.thenReturn("redirect:/list"); 
	}
	
	/**
	 * Chunked response with backpressure.
	 * Uses max-chunk-size=1024 defined in application.properties.
	 */
	@GetMapping("/list-chunked")
	public Mono<String> listarProductsChunked(Model model) {
		model.addAttribute("title", "Product List");
		
		Flux<Product> products = productService.findAllNameUppercase()
				.repeat(500);
		
		model.addAttribute("products", products);
		return Mono.just("list-chunked");
	} 
	
	/**
	 * Reactive Data Driver with controlled data flow.
	 * Sends data in chunks of 2 with 1-second delays between elements.
	 */
	@GetMapping("/list")
	public String listarReactiveDataDriver(Model model) {
		model.addAttribute("title", "Product List");
		
		Flux<Product> products = productService.findAll()
				.delayElements(Duration.ofSeconds(1));
		
		model.addAttribute("products", new ReactiveDataDriverContextVariable(products, 2));
		return "listProducts";
	} 
}
