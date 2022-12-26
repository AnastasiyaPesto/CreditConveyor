package ru.zentsova.deal.feign.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.zentsova.deal.model.LoanApplicationRequestDto;
import ru.zentsova.deal.model.LoanOfferDto;

import java.util.List;

@FeignClient(value = "conveyorClient", url = "http://localhost:8080/conveyor/")
public interface ConveyorServiceClient {
    @RequestMapping(method = RequestMethod.POST, value = "/offers")
    ResponseEntity<List<LoanOfferDto>> getAllPossibleOffers(LoanApplicationRequestDto loanApplicationRequestDTO);
}
