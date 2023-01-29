package ru.zentsova.deal.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zentsova.deal.exceptions.EntityNotExistException;
import ru.zentsova.deal.model.*;
import ru.zentsova.deal.repositories.ApplicationRepository;
import ru.zentsova.deal.services.ApplicationService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Value("${exception.message.application-not-found}")
    private String applicationNotFound;

    private final ApplicationRepository applicationRepository;

    @Transactional
    public Application createAndSaveNewApplication(Client client) {
        Application application = new Application();
        application.setClient(client);
        setStatus(application, ApplicationStatus.PREAPPROVAL, ApplicationStatusHistoryDto.ChangeTypeEnum.AUTOMATIC);
        Application createdApplication = applicationRepository.save(application);
        log.info("Application {} was created and saved", createdApplication);

        return createdApplication;
    }

    @Transactional
    public void update(Application application, AppliedOffer appliedOffer) {
        application.setAppliedOffer(appliedOffer);
        log.info("Application id={} was updated: applied_offer_id={}", application.getId(), appliedOffer.getApplicationId());
    }

    @Transactional
    public void update(Application application, Credit credit, ApplicationStatus status, ApplicationStatusHistoryDto.ChangeTypeEnum changeType) {
        setStatus(application, status, changeType);
        application.setCredit(credit);
        log.info("Application id={} was updated: credit_id={}", application.getId(), credit.getId());
    }

    @Transactional(readOnly = true)
    public Application findById(Long id) {
        try {
            return applicationRepository.findById(id).orElseThrow();
        } catch (NoSuchElementException ex) {
            log.debug(String.format(applicationNotFound, id));
            throw new EntityNotExistException(String.format(applicationNotFound, id));
        }
    }

    @Transactional(readOnly = true)
    public void setStatus(Application application, ApplicationStatus status, ApplicationStatusHistoryDto.ChangeTypeEnum changeType) {
        application.setStatus(status);
        addStatusHistory(application, status, changeType);
    }

    private void addStatusHistory(Application application, ApplicationStatus status, ApplicationStatusHistoryDto.ChangeTypeEnum changeType) {
        ApplicationStatusHistoryDto statusHistory = new ApplicationStatusHistoryDto().status(status.name()).time(LocalDateTime.now().toString()).changeType(changeType);
        if (application.getStatusHistory() == null)
            application.setStatusHistory(new ArrayList<>(Collections.singletonList(statusHistory)));
        else
            application.getStatusHistory().add(statusHistory);
    }
}
