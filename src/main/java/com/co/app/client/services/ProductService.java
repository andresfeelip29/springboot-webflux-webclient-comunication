package com.co.app.client.services;

import com.co.app.client.models.Product;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    Flux<Product> findAll();

    Mono<Product> findById(String id);

    Mono<Product> save(Product product);

    Mono<Product> update(Product product, String id);

    Mono<Void> delete(String id);

    Mono<Product> upload(FilePart filePart, String id);
}
