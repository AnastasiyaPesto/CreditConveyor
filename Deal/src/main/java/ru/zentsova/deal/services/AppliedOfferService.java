package ru.zentsova.deal.services;

import org.springframework.stereotype.Service;
import ru.zentsova.deal.model.AppliedOffer;

@Service
public interface AppliedOfferService {

    AppliedOffer save(AppliedOffer appliedOffer);
}