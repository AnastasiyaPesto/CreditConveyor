package ru.zentsova.application.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.zentsova.application.model.LoanApplicationRequestDto;
import ru.zentsova.application.model.LoanOfferDto;

import java.util.List;

@Service
public interface DealService {

    ResponseEntity<List<LoanOfferDto>> getAllPossibleOffers(LoanApplicationRequestDto loanApplicationRequestDto);
}