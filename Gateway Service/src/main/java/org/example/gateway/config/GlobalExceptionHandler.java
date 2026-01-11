package org.example.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Order(-2)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        logger.error("Error handling request: {}", exchange.getRequest().getURI(), ex);
        
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Internal Server Error";
        
        if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            status = HttpStatus.resolve(responseStatusException.getStatusCode().value());
            message = responseStatusException.getReason();
        } else if (ex.getMessage() != null) {
            message = ex.getMessage();
        }
        
        String errorResponse = String.format(
            "{\"error\":\"%s\",\"status\":%d,\"message\":\"%s\",\"path\":\"%s\"}",
            ex.getClass().getSimpleName(),
            status.value(),
            message,
            exchange.getRequest().getURI().getPath()
        );
        
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        DataBuffer buffer = exchange.getResponse().bufferFactory()
            .wrap(errorResponse.getBytes(StandardCharsets.UTF_8));
        
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
