package ru.zentsova.deal.services.impl;

import org.springframework.stereotype.Service;
import ru.zentsova.deal.model.LoanOfferDto;
import ru.zentsova.deal.services.ConveyorService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConveyorServiceImpl implements ConveyorService {

    @Override
    public void setApplicationId(List<LoanOfferDto> offers, long id) {
        offers.forEach(offer -> offer.setApplicationId(id));
    }

    @Override
    public List<LoanOfferDto> sort(List<LoanOfferDto> offers, Comparator<LoanOfferDto> comparator) {
        return offers.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}
