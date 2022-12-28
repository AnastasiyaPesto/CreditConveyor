package ru.zentsova.deal.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zentsova.deal.model.AppliedOffer;
import ru.zentsova.deal.repositories.AppliedOfferRepository;
import ru.zentsova.deal.services.AppliedOfferService;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AppliedOfferServiceImpl implements AppliedOfferService {

    @Value("${log.message.was-saved}")
    private String msgWasSaved;

    private final AppliedOfferRepository appliedOfferRepository;

    @Transactional
    public AppliedOffer save(AppliedOffer appliedOffer) {
        AppliedOffer createdAppliedOffer = appliedOfferRepository.save(appliedOffer);
        log.info(msgWasSaved, createdAppliedOffer);

        return createdAppliedOffer;
    }
}