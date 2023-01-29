package ru.zentsova.deal.services;

import org.springframework.stereotype.Service;
import ru.zentsova.deal.model.Credit;

@Service
public interface CreditService {

    Credit save(Credit credit);
}