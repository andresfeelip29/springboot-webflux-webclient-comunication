package com.co.app.client.services;

import com.co.app.client.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
public class ProductServiceImpl implements ProductService {


    private final WebClient.Builder webClient;

    @Autowired
    public ProductServiceImpl(WebClient.Builder webClient) {
        this.webClient = webClient;
    }

    @Override
    public Flux<Product> findAll() {
        return this.webClient.build().get().
                accept(MediaType.APPLICATION_JSON)
                .exchangeToFlux(clientResponse -> clientResponse.bodyToFlux(Product.class));
    }

    @Override
    public Mono<Product> findById(String id) {
        return this.webClient.build().get()
                .uri("/{id}", Collections.singletonMap("id", id))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Product.class);
        //.exchangeToMono(clientResponse -> clientResponse.bodyToMono(Product.class));
    }

    @Override
    public Mono<Product> save(Product product) {
        return this.webClient.build().post()
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(product))
                .retrieve()
                .bodyToMono(Product.class);
    }

    @Override
    public Mono<Product> update(Product product, String id) {
        return this.webClient.build().put()
                .uri("/{id}", Collections.singletonMap("id", id))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(product))
                .retrieve()
                .bodyToMono(Product.class);
    }

    @Override
    public Mono<Void> delete(String id) {
        return this.webClient.build().delete()
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<Product> upload(FilePart filePart, String id) {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.asyncPart("file", filePart.content(), DataBuffer.class)
                .headers(httpHeaders -> {
                    httpHeaders.setContentDispositionFormData("file", filePart.filename());
                });
        return this.webClient.build().post().uri("/upload/{id}", Collections.singletonMap("id", id))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(multipartBodyBuilder.build())
                .retrieve()
                .bodyToMono(Product.class);
    }
}
