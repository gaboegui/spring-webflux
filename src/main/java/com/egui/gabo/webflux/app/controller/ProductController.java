package com.egui.gabo.webflux.app.controller;

import java.io.File;
import java.net.MalformedURLException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;

import com.egui.gabo.webflux.app.models.document.Category;
import com.egui.gabo.webflux.app.models.document.Product;
import com.egui.gabo.webflux.app.service.ProductService;


import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Thymeleaf controller for product views. Demonstrates various reactive
 * streaming patterns with Thymeleaf templates.
 * 
 * SessionAttributes avoid the use of hidden input to indentify save or update
 * action
 * 
 * @author Gabriel Eguiguren P.
 */
@SessionAttributes("product")
@Controller
public class ProductController {

	private static final Logger log = LoggerFactory.getLogger(ProductController.class);

	// @ModelAttribute pass data to Model for use in View
	@ModelAttribute("categories")
	public Flux<Category> getCategories() {
		return productService.findAllCategories();
	}

	// loads from application.properties
	@Value("${config.upload.path}")
	String uploadDirectory;

	@Autowired
	private ProductService productService;

	/**
	 * Standard product listing with uppercase names. Uses default Thymeleaf
	 * subscription to Flux.
	 */
	@GetMapping("/")
	public Mono<String> listarProductos(Model model) {
		model.addAttribute("title", "Product List");

		Flux<Product> products = productService.findAllNameUppercase();

		products.subscribe(prod -> log.info(prod.getName()));
		model.addAttribute("products", products);

		return Mono.just("listProducts"); // must match name of html file in resources/static
	}

	/**
	 * Large dataset demonstration (500x repeated). Tests performance with large
	 * reactive streams.
	 */
	@GetMapping("/list-huge")
	public Mono<String> listarProductosFull(Model model) {
		model.addAttribute("title", "Product List");

		Flux<Product> products = productService.findAllNameUppercase().repeat(500);

		model.addAttribute("products", products);
		return Mono.just("listProducts");
	}

	@GetMapping("/product-form")
	public Mono<String> createForm(Model model) {

		model.addAttribute("title", "Product Form");
		model.addAttribute("buttonText", "Create");
		model.addAttribute("product", new Product()); // must match @SessionAttributes
		return Mono.just("productForm");
	}

	@GetMapping("/product-form/{id}")
	public Mono<String> updateForm(@PathVariable String id, Model model) {

		Mono<Product> productDb = productService.findById(id).defaultIfEmpty(new Product()); // avoids error message if
																								// not found

		model.addAttribute("title", "Product Edit");
		model.addAttribute("buttonText", "Edit");
		model.addAttribute("product", productDb); // must match @SessionAttributes

		return Mono.just("productForm");
	}

	@GetMapping("/delete/{id}")
	public Mono<String> deleteProduct(@PathVariable String id) {

		return productService.findById(id).defaultIfEmpty(new Product()).flatMap(prod -> { // notFound -> Exception
			if (prod.getId() == null) {
				return Mono.error(new InterruptedException("Product not found"));
			}
			return Mono.just(prod);
		}).flatMap(productService::delete) // perform delete
				.then(Mono.just("redirect:/list?success=Producto+eliminado")) // replaces Mono<Void>
				.onErrorResume(ex -> Mono.just("redirect:/list?success=no+existe+Producto")); // handle Exception
	}
	
	
	@GetMapping("/uploads/img/{picName:.+}")
	public Mono<ResponseEntity<Resource>> displayPicture(@PathVariable String picName ) throws MalformedURLException{
		
		Path absolutePath = Paths.get(uploadDirectory).resolve(picName).toAbsolutePath();
		
		Resource image = new UrlResource(absolutePath.toUri());
		
		return Mono.just(
				ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFilename() +"\"")
					.body(image)
				);
		
	}

	@PostMapping("/product-form")
	public Mono<String> saveForm(@Valid Product product, BindingResult validation, Model model,
			@RequestPart FilePart file, SessionStatus session) {

		if (validation.hasErrors()) {

			model.addAttribute("title", "Product Edit");
			model.addAttribute("buttonText", "Save");

			return Mono.just("productForm");

		} else {

			session.setComplete(); // destroys the object from @SessionAttributes

			// retrieves the id from Form
			Mono<Category> category = productService.findCategoryById(product.getCategory().getId());

			return category.flatMap(cat -> {

				if (!file.filename().isEmpty()) {
					product.setPicture(UUID.randomUUID().toString() + "-"
							+ file.filename().replace(" ", "").replace(":", "").replace("\\", ""));
				}

				if (product.getCreateAt() == null) {
					product.setCreateAt(new Date());
				}
				product.setCategory(cat);
				return productService.save(product);
			}).doOnNext(p -> {
				log.info(p.getName());
			}).flatMap(p -> {
				if (!file.filename().isEmpty()) {
					return file.transferTo(new File(uploadDirectory + p.getPicture()));
				}
				return Mono.empty();
			}).thenReturn("redirect:/list?success=Producto+guardado");

		}
	}

	/**
	 * Chunked response with backpressure. Uses max-chunk-size=1024 defined in
	 * application.properties.
	 */
	@GetMapping("/list-chunked")
	public Mono<String> listarProductsChunked(Model model) {
		model.addAttribute("title", "Product List");

		Flux<Product> products = productService.findAllNameUppercase().repeat(500);

		model.addAttribute("products", products);
		return Mono.just("list-chunked");
	}

	/**
	 * Reactive Data Driver with controlled data flow. Sends data in chunks of 2
	 * with 1-second delays between elements.
	 */
	@GetMapping("/list")
	public String listarReactiveDataDriver(Model model) {
		model.addAttribute("title", "Product List");

		Flux<Product> products = productService.findAll().delayElements(Duration.ofSeconds(1));

		model.addAttribute("products", new ReactiveDataDriverContextVariable(products, 2));
		return "listProducts";
	}
}
