package ru.zentsova.deal.services;

import liquibase.pro.packaged.E;
import org.springframework.stereotype.Service;
import ru.zentsova.deal.model.LoanOfferDto;

import java.util.List;

@Service
public interface ConveyorService {

    void setApplicationId(List<LoanOfferDto> offers, long id);
    List<LoanOfferDto> sortByRate(List<LoanOfferDto> offers);
}
