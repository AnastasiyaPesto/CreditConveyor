package ru.zentsova.deal.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zentsova.deal.feign.exceptions.EntityNotExistException;
import ru.zentsova.deal.model.*;
import ru.zentsova.deal.repositories.ApplicationRepository;
import ru.zentsova.deal.repositories.AppliedOfferRepository;
import ru.zentsova.deal.services.ApplicationService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ApplicationServiceImpl implements ApplicationService {

    @Value("${exception.message.application-was-not-found}")
    private String applicationWasNotFound;

    private final ApplicationRepository applicationRepository;

    @Transactional
    public Application createAndSaveNewApplication(Client client) {
        Application application = new Application();
        application.setClient(client);
        setStatus(application, ApplicationStatus.PREAPPROVAL, ChangeType.AUTOMATIC);
        Application createdApplication = applicationRepository.save(application);

        log.info("Application {} was created and saved", createdApplication);

        return createdApplication;
    }

    @Transactional
    public void update(Application application, AppliedOffer appliedOffer) {
        application.setAppliedOffer(appliedOffer);
        // todo add log
    }

    @Transactional
    public void update(Application application, Credit credit, ApplicationStatus status, ChangeType changeType) {
        setStatus(application, status, changeType);
        application.setCredit(credit);
    }

    public Application findById(Long id) throws EntityNotExistException {
        try {
            return applicationRepository.findById(id).orElseThrow();
        } catch (NoSuchElementException ex) {
            log.debug(String.format(applicationWasNotFound, id));
            throw new EntityNotExistException(String.format(applicationWasNotFound, id));
        }
    }

    public void setStatus(Application application, ApplicationStatus status, ChangeType changeType) {
        application.setStatus(status);
        addStatusHistory(application, status, changeType);
    }

    private void addStatusHistory(Application application, ApplicationStatus status, ChangeType changeType) {
        StatusHistory statusHistory = new StatusHistory().status(status.name()).time(LocalDateTime.now()).changeType(changeType);
        if (application.getStatusHistory() == null)
            application.setStatusHistory(new ArrayList<>(Collections.singletonList(statusHistory)));
        else
            application.getStatusHistory().add(statusHistory);
    }
}
