package ru.zentsova.deal.config;


import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.zentsova.deal.feign.exceptions.ConveyorErrorEncoder;

@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ConveyorErrorEncoder();
    }
}
