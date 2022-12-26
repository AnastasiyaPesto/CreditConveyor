package ru.zentsova.deal.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zentsova.deal.model.Client;
import ru.zentsova.deal.model.Passport;
import ru.zentsova.deal.repositories.ClientRepository;
import ru.zentsova.deal.services.ClientService;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    @Transactional
    public Client save(Client client, Passport passport) {
        client.setPassportId(passport);
        Client savedClient = clientRepository.save(client);
        log.info("{} is saved", savedClient);
        return savedClient;
    }
}
