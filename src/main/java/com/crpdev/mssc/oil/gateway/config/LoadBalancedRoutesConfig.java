package com.crpdev.mssc.oil.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by rajapandian
 * Date: 14/09/20
 * Project: mssc-oil-eureka
 * Package: com.crpdev.mssc.oil.gateway.config
 **/
@Profile("local-discovery")
@Configuration
public class LoadBalancedRoutesConfig {

    @Bean
    public RouteLocator loadBalancedRoutes(RouteLocatorBuilder builder){
        return builder.routes()
                .route(r-> r.path("/api/v1/oil*", "/api/v1/oil/*","/api/v1/oil/productCode/*")
                    .uri("lb://oil-service")
                    .id("oil-service"))
                .route(r -> r.path("/api/v1/customers/**")
                    .uri("lb://oil-order-service")
                .   id("oil-order-service"))
                .route(r -> r.path("/api/v1/oil/*/inventory")
                        .filters(f -> f.circuitBreaker(c -> c.setName("inventory-circuit-breaker")
                                                            .setFallbackUri("forward:/oil-inventory-failover")
                                                            .setRouteId("inventory-failover")
                        ))
                    .uri("lb://oil-inventory-service")
                    .id("oil-inventory-service"))
                .route(r -> r.path("/oil-inventory-failover/**")
                    .uri("lb://oil-inventory-failover")
                    .id("oil-inventory-failover-service"))
                .build();
    }
}
