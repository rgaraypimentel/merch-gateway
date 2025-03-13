package com.qallpatic.merch.gateway.beans;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;

@Configuration
public class GatewayBeans {

//	@Bean
//	@Profile("eureka-off")
//	public RouteLocator routeLocatorEurekaOff(RouteLocatorBuilder builder) {
//		return builder.routes()
//                .route(route  -> route
//                		.path("/qallpa-merch/**")
//                		.uri("http://localhost:54018"))
//                .build();
//	}
	
	@Bean
//	@Profile("eureka-on")
	public RouteLocator routeLocator(RouteLocatorBuilder builder) {
	    return builder.routes()
	        .route(route -> route
	            .path("/qallpa-merch/**")
	            .filters(f -> f
	                .preserveHostHeader()
	                .removeRequestHeader("X-Forwarded-Host")
	                .removeRequestHeader("X-Forwarded-Port")
	                .addRequestHeader("X-Forwarded-Proto", "http")
	                .filter((exchange, chain) -> {
	                    // Extraer la cookie de sesión
	                    String sessionId = exchange.getRequest().getCookies()
	                        .getFirst("SESSION") != null
	                        ? exchange.getRequest().getCookies().getFirst("SESSION").getValue()
	                        : null;

	                    // Construir una nueva request con la sesión en la cookie
	                    ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate();
	                    if (sessionId != null) {
	                        requestBuilder.header("X-Session-Id", sessionId);
	                        requestBuilder.header("Cookie", "SESSION=" + sessionId);
	                    }

	                    ServerHttpRequest mutatedRequest = requestBuilder.build();
	                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
	                })
	            )
	            .uri("lb://qallpa-merch"))
	        .build();
	}
}
