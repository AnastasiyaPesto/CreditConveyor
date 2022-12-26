package ru.zentsova.deal.services;

import org.springframework.stereotype.Service;
import ru.zentsova.deal.model.Application;
import ru.zentsova.deal.model.Client;

 @Service
public interface ApplicationService {
    Application createApplicationAndSave(Client client);
}
