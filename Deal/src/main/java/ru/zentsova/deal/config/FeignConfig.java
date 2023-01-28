package ru.zentsova.deal.config;


import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.zentsova.deal.exceptions.ConveyorErrorDecoder;

@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ConveyorErrorDecoder();
    }
}
