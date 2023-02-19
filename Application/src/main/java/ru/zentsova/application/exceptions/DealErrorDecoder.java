package ru.zentsova.application.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import ru.zentsova.application.model.ApplicationError;

import java.io.IOException;
import java.io.InputStream;

public class DealErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        ApplicationError message;
        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            message = mapper.readValue(bodyIs, ApplicationError.class);
        } catch (IOException e) {
            return new RuntimeException(e.getMessage());
        }
        if (response.status() == 400) {
            return new DealException(message.getMessage() != null ? message.getMessage() : HttpStatus.BAD_REQUEST.name());
        }
        return errorDecoder.decode(methodKey, response);
    }
}