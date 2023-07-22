package com.co.app.client.handler;

import com.co.app.client.models.Product;
import com.co.app.client.services.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class ProductHandler {

    private final ProductServiceImpl productService;

    @Autowired
    public ProductHandler(ProductServiceImpl productService) {
        this.productService = productService;
    }

    public Mono<ServerResponse> listProduct(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.productService.findAll(), Product.class);
    }

    public Mono<ServerResponse> detailProduct(ServerRequest request) {
        String id = request.pathVariable("id");
        return productService.findById(id)
                .flatMap(productEntity -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(productEntity))
                ).switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).build());

    }

    public Mono<ServerResponse> saveProduct(ServerRequest request) {
        return this.errorHandler(request.bodyToMono(Product.class)
                .flatMap(product -> {
                    if (Objects.isNull(product.getCreateAt())) {
                        product.setCreateAt(new Date());
                    }
                    return this.productService.save(product);
                }).flatMap(product -> ServerResponse.created(URI.create("api/cliente/".concat(product.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(product), Product.class))
        );

    }

    public Mono<ServerResponse> updateProduct(ServerRequest request) {
        return this.errorHandler(
                request.bodyToMono(Product.class)
                        .flatMap(product -> this.productService.update(product, request.pathVariable("id")))
                        .flatMap(productResponse -> ServerResponse.created(URI.create("api/cliente/".concat(productResponse.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromValue(productResponse), Product.class)));

    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        return this.productService.delete(request.pathVariable("id")).then(ServerResponse.noContent().build())
                .onErrorResume(err -> {
                    WebClientResponseException errResponse = (WebClientResponseException) err;
                    if (errResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return ServerResponse.notFound().build();
                    }
                    return Mono.error(errResponse);
                });
    }

    public Mono<ServerResponse> uploadPhoto(ServerRequest request) {
        return this.errorHandler(request.multipartData()
                .map(stringPartMultiValueMap -> stringPartMultiValueMap.toSingleValueMap().get("file"))
                .cast(FilePart.class)
                .flatMap(filePart -> this.productService.upload(filePart, request.pathVariable("id")))
                .flatMap(product -> ServerResponse.created(URI.create("/api/client/".concat(product.getId())))
                        .contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(product), Product.class))
        );

    }

    private Mono<ServerResponse> errorHandler(Mono<ServerResponse> response) {
        return response.onErrorResume(err -> {
            WebClientResponseException errResponse = (WebClientResponseException) err;
            if (errResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                Map<String, Object> responseErr = new HashMap<>();
                responseErr.put("error", "Product no exist");
                responseErr.put("timestamp", new Date());
                responseErr.put("status", errResponse.getStatusCode());
                return ServerResponse.status(HttpStatus.NOT_FOUND).body(BodyInserters.fromValue(responseErr));
            }
            return Mono.error(errResponse);
        });
    }

}
