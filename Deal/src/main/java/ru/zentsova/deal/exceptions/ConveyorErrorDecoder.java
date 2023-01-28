package ru.zentsova.deal.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import ru.zentsova.deal.model.ApplicationError;

import java.io.IOException;
import java.io.InputStream;


public class ConveyorErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        ApplicationError message;
        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            message = mapper.readValue(bodyIs, ApplicationError.class);
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }
        if (response.status() == 400) {
            return new ConveyorException(message.getMessage() != null ? message.getMessage() : HttpStatus.BAD_REQUEST.name());
        }
        return errorDecoder.decode(methodKey, response);
    }

}
