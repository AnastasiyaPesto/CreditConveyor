package ru.zentsova.application.clients.deal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import ru.zentsova.application.model.LoanApplicationRequestDto;
import ru.zentsova.application.model.LoanOfferDto;

import java.util.List;

@FeignClient(name = "${feign.name}", url = "${feign.url}")
public interface DealClient {

    @PostMapping(path = "/application")
    ResponseEntity<List<LoanOfferDto>> getAllPossibleOffers(LoanApplicationRequestDto loanApplicationRequestDto);

    @PutMapping(path = "/offer")
    ResponseEntity<Void> chooseOneOffer(LoanOfferDto loanOfferDto);
}