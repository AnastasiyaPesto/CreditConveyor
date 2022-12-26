package ru.zentsova.deal.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zentsova.deal.model.Application;
import ru.zentsova.deal.model.Client;
import ru.zentsova.deal.repositories.ApplicationRepository;
import ru.zentsova.deal.services.ApplicationService;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;

    @Override
    @Transactional
    public Application createApplicationAndSave(Client client) {
        Application application = new Application();
        application.setClient(client);
        Application savedApplication = applicationRepository.save(application);
        log.info("Application {} is created and saved", savedApplication);

        return savedApplication;
    }
}
