package com.portalapp.portalapp.Configurations;

//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");  // Topic for messages
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Allow connections from the specified origin
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3001")  // Allow frontend to connect
                .withSockJS();

        // Add logging to track WebSocket registration
        System.out.println("[WebSocket Debug] Registered WebSocket endpoint: /ws");
    }
    /* 
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Allow CORS for WebSocket connections
                registry.addMapping("/ws/**")
                        .allowedOrigins("http://localhost:3001")  // Allow frontend to connect
                        .allowedMethods("GET", "POST", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
        */
}
