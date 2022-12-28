package ru.zentsova.deal.services;

import org.springframework.stereotype.Service;
import ru.zentsova.deal.model.*;

@Service
public interface ApplicationService {

    Application findById(Long id);
    Application createAndSaveNewApplication(Client client);
    void update(Application application, AppliedOffer appliedOffer);
    void update(Application application, Credit credit, ApplicationStatus status, ChangeType changeType);
}
