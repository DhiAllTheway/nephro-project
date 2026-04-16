package tn.esprit.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("vital-signs-service", r -> r
                        .path("/vital-signs/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://vital-signs-service"))
                .route("transplant-service", r -> r
                        .path("/transplant/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://transplant-service"))
                .build();
    }
}