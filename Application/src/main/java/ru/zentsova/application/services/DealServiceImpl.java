package ru.zentsova.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.zentsova.application.clients.deal.DealClient;
import ru.zentsova.application.model.LoanApplicationRequestDto;
import ru.zentsova.application.model.LoanOfferDto;
import ru.zentsova.application.util.validator.LoanApplicationRequestDtoValidator;

import java.util.List;

@Service
public class DealServiceImpl implements DealService {

    private final DealClient dealClient;
    private final LoanApplicationRequestDtoValidator validator;

    @Autowired
    public DealServiceImpl(DealClient dealClient, LoanApplicationRequestDtoValidator validator) {
        this.dealClient = dealClient;
        this.validator = validator;
    }

    public ResponseEntity<List<LoanOfferDto>> getAllPossibleOffers(LoanApplicationRequestDto loanApplicationRequestDto) {
        if (validator.validate(loanApplicationRequestDto, true))
            return dealClient.getAllPossibleOffers(loanApplicationRequestDto);

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}