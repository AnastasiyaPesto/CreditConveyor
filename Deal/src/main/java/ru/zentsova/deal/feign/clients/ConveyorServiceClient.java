package ru.zentsova.deal.feign.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.zentsova.deal.model.CreditDto;
import ru.zentsova.deal.model.LoanApplicationRequestDto;
import ru.zentsova.deal.model.LoanOfferDto;
import ru.zentsova.deal.model.ScoringDataDto;

import java.util.List;

@FeignClient(name = "${feign.name}", url = "${feign.url}")
public interface ConveyorServiceClient {

    @RequestMapping(method = RequestMethod.POST, value = "/offers")
    ResponseEntity<List<LoanOfferDto>> getAllPossibleOffers(LoanApplicationRequestDto loanApplicationRequestDTO);

    @RequestMapping(method = RequestMethod.POST, value = "/calculation")
    ResponseEntity<CreditDto> getFullCalculatedParameters(ScoringDataDto scoringDataDto);
}
