package com.egui.gabo.webflux.app;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.egui.gabo.webflux.app.models.document.Product;
import com.egui.gabo.webflux.app.models.repository.ProductRepository;

import reactor.core.publisher.Flux;

/**
 * Main Spring Boot application class for Spring WebFlux with MongoDB Reactive.
 * Demonstrates reactive programming patterns and automatic test data setup.
 * 
 * @author Gabriel Eguiguren P.
 */
@SpringBootApplication
public class SpringWebfluxApplication implements CommandLineRunner {
	
	private static final Logger log = LoggerFactory.getLogger(SpringWebfluxApplication.class);

	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private ReactiveMongoTemplate mongoTemplate;
	
	
	public static void main(String[] args) {
		SpringApplication.run(SpringWebfluxApplication.class, args);
	}

	/**
	 * Executes on application startup.
	 * Clears existing products and inserts test data for development.
	 */
	@Override
	public void run(String... args) throws Exception {
		// Clear existing test data
		mongoTemplate.dropCollection("products").subscribe();
		
		// Insert test products for development environment
		Flux.just(
				new Product("TV LG 4k 52in", 500.99),
				new Product("Camara Sony", 500.99),
				new Product("Apple watch", 200.99),
				new Product("Laptop Lenovo", 700.99),
				new Product("Webcam Logitech", 199.99),
				new Product("Camara Sony", 500.99),
				new Product("TV Haisen 4k 52", 600.99)
			)
			.flatMap(product -> {
				product.setCreateAt(new Date());
				return repository.save(product);
			})
			.subscribe(product -> log.info("Inserted: {}", product.getName()));
	}
}
