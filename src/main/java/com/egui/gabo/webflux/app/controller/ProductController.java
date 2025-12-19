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
import com.egui.gabo.webflux.app.models.repository.ProductRepository;

import reactor.core.publisher.Flux;

@Controller
public class ProductController {
	
	private static final Logger log = LoggerFactory.getLogger(ProductController.class);	
	
	@Autowired
	private ProductRepository productDao;
	
	@GetMapping("/")
	public String listarProductos(Model model) {
		
		// model to work with thymeleaf
		model.addAttribute("title","Product List");
		
		// Flux<Product> productos = productDao.findAll();
		Flux<Product> products = productDao.findAll()
				.map(product-> {
					product.setName(product.getName().toUpperCase());
					return product;
				});
		
		// add another suscriber to default of thymeleaf
		products.subscribe(prod -> log.info(prod.getName()));
		
		model.addAttribute("products", products);  // thymeleaf by default subscribe() to Flux
		
		return "listProducts";  // must match with templates/html file name
	} 
	
	
	/**

	 * @param model
	 * @return
	 */
	@GetMapping("/list-huge")
	public String listarProductosFull(Model model) {
		
		// model to work with thymeleaf
		model.addAttribute("title","Product List");
		
		// Flux<Product> productos = productDao.findAll();
		Flux<Product> products = productDao.findAll()
				.map(product-> {
					product.setName(product.getName().toUpperCase());
					return product;
				})
				.repeat(500);  // Repeat the existing to increase size

		
		model.addAttribute("products", products);  // thymeleaf by default subscribe() to Flux
		
		return "listProducts";  // must match with templates/html file name
	}
	
	/**
	 * This view uses Contra-presure definition.
	 * Defined by list in application.properties
	 * Uses max-chunk-size=1024 defined to send the data chunked
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/list-chunked")
	public String listarProductsChunked(Model model) {
		
		// model to work with thymeleaf
		model.addAttribute("title","Product List");
		
		// Flux<Product> productos = productDao.findAll();
		Flux<Product> products = productDao.findAll()
				.map(product-> {
					product.setName(product.getName().toUpperCase());
					return product;
				})
				.repeat(500);  // Repeat the existing to increase size

		
		model.addAttribute("products", products);  // thymeleaf by default subscribe() to Flux
		
		return "list-chunked";  // must match with templates/html file name
	} 

	
	
	/**
	 * Uses ReactiveDataDriverContextVariable to send data partially
	 * @param model for use in thymeleaf
	 * @return
	 */
	@GetMapping("/list")
	public String listarReactiveDataDriver(Model model) {
		
		// model to work with thymeleaf
		model.addAttribute("title","Product List");
		
		// Flux<Product> productos = productDao.findAll();
		Flux<Product> products = productDao.findAll()
				.map(product-> {
					product.setName(product.getName().toUpperCase());
					return product;
				})
				.delayElements(Duration.ofSeconds(1));
		
		//Send blocks of 2 data until delay completed
		model.addAttribute("products", new ReactiveDataDriverContextVariable(products, 2));  
		
		return "listProducts";  // must match with templates/html file name
	} 
}
