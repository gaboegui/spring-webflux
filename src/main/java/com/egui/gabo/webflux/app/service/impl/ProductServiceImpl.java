package com.egui.gabo.webflux.app.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.egui.gabo.webflux.app.models.document.Product;
import com.egui.gabo.webflux.app.models.repository.ProductRepository;
import com.egui.gabo.webflux.app.service.ProductService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService {
	
	@Autowired
	private ProductRepository productDao;

	@Override
	public Flux<Product> findAll() {
		return productDao.findAll();
	}
	
	@Override
	public Flux<Product> findAllNameUppercase() {
		return productDao.findAll()
				.map(product -> {
					product.setName(product.getName().toUpperCase());
					return product;
		});
	}


	@Override
	public Mono<Product> findById(String id) {
		return productDao.findById(id);
	}

	@Override
	public Mono<Product> save(Product p) {
		return productDao.save(p);
	}

	@Override
	public Mono<Void> delete(Product p) {
		return productDao.delete(p);
	}

}
