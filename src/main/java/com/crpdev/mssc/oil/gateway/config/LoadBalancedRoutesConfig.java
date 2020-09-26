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
@Profile({"local-discovery", "digitalocean"})
@Configuration
public class LoadBalancedRoutesConfig {

    @Bean
    public RouteLocator loadBalancedRoutes(RouteLocatorBuilder builder){
        return builder.routes()
                .route(r-> r.path("/api/v1/oil*", "/api/v1/oil/*","/api/v1/oil/productCode/*")
                    .uri("lb://oil-service")
                    .id("oil-service"))
                .route(r -> r.path("/api/v1/customers/**")
                    .uri("lb://order-service")
                    .id("order-service"))
                .route(r -> r.path("/api/v1/oil/*/inventory")
                        .filters(f -> f.circuitBreaker(c -> c.setName("inventory-circuit-breaker")
                                                            .setFallbackUri("forward:/inventory-failover")
                                                            .setRouteId("inventory-failover-service")
                        ))
                    .uri("lb://inventory-service")
                    .id("inventory-service"))
                .route(r -> r.path("/inventory-failover/**")
                    .uri("lb://inventory-failover")
                    .id("inventory-failover-service"))
                .build();
    }
}
