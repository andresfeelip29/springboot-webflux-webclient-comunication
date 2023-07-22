package com.co.app.client;


import com.co.app.client.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(ProductHandler productHandler) {
        return RouterFunctions.route(RequestPredicates.GET("/api/client"), productHandler::listProduct)
                .andRoute(RequestPredicates.GET("/api/client/{id}"), productHandler::detailProduct)
                .andRoute(RequestPredicates.POST("/api/client"), productHandler::saveProduct)
                .andRoute(RequestPredicates.PUT("/api/client/{id}"), productHandler::updateProduct)
                .andRoute(RequestPredicates.GET("/api/client/{id}"), productHandler::deleteProduct)
                .andRoute(RequestPredicates.POST("/api/client/upload/{id}"), productHandler::uploadPhoto);
    }
}
