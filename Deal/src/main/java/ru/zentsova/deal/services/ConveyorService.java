package ru.zentsova.deal.services;

import org.springframework.stereotype.Service;
import ru.zentsova.deal.model.LoanOfferDto;

import java.util.Comparator;
import java.util.List;

@Service
public interface ConveyorService {

    void setApplicationId(List<LoanOfferDto> offers, long id);
    List<LoanOfferDto> sort(List<LoanOfferDto> offers, Comparator<LoanOfferDto> comparator);
}
