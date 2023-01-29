package ru.zentsova.deal.services;

import org.springframework.stereotype.Service;
import ru.zentsova.deal.model.Application;
import ru.zentsova.deal.model.ApplicationStatus;
import ru.zentsova.deal.model.ApplicationStatusHistoryDto;
import ru.zentsova.deal.model.AppliedOffer;
import ru.zentsova.deal.model.Credit;
import ru.zentsova.deal.model.Client;

@Service
public interface ApplicationService {

    Application findById(Long id);
    Application createAndSaveNewApplication(Client client);
    void update(Application application, AppliedOffer appliedOffer);
    void update(Application application, Credit credit, ApplicationStatus status, ApplicationStatusHistoryDto.ChangeTypeEnum changeType);
}
