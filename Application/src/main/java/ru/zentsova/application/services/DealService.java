package ru.zentsova.application.services;

import org.springframework.http.ResponseEntity;
import ru.zentsova.application.model.LoanApplicationRequestDto;
import ru.zentsova.application.model.LoanOfferDto;

import java.util.List;

public interface DealService {

    ResponseEntity<List<LoanOfferDto>> getAllPossibleOffers(LoanApplicationRequestDto loanApplicationRequestDto);

    ResponseEntity<Void> chooseOneOffer(LoanOfferDto loanOfferDto);
}