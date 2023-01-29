package ru.zentsova.deal.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zentsova.deal.model.Credit;
import ru.zentsova.deal.model.CreditStatus;
import ru.zentsova.deal.repositories.CreditRepository;
import ru.zentsova.deal.services.CreditService;

@Slf4j
@RequiredArgsConstructor
@Service
public class CreditServiceImpl implements CreditService {

    @Value("${log.message.saved}")
    private String msgSaved;

    private final CreditRepository creditRepository;

    @Transactional
    public Credit save(Credit credit) {
        credit.setCreditStatus(CreditStatus.CALCULATED);
        Credit createdCredit = creditRepository.save(credit);
        log.info(msgSaved, createdCredit);

        return createdCredit;
    }
}