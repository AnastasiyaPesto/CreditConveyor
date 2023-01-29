package ru.zentsova.deal.services;

import org.springframework.stereotype.Service;
import ru.zentsova.deal.model.Client;
import ru.zentsova.deal.model.Passport;

@Service
public interface ClientService {

    Client save(Client client, Passport passport);
}
