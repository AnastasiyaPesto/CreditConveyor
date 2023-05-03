package ru.zentsova.deal.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zentsova.deal.mappers.EmploymentMapper;
import ru.zentsova.deal.model.Client;
import ru.zentsova.deal.model.FinishRegistrationRequestDto;
import ru.zentsova.deal.model.Passport;
import ru.zentsova.deal.repositories.ClientRepository;
import ru.zentsova.deal.services.ClientService;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {

    @Value("${log.message.saved}")
    private String msgSaved;

    private final ClientRepository clientRepository;
    private final EmploymentMapper empMapper;

    @Transactional
    public Client save(Client client, Passport passport) {
        client.setPassportId(passport);
        Client createdClient = clientRepository.save(client);
        log.info(msgSaved, createdClient);

        return createdClient;
    }

    @Transactional
    public void update(long id, FinishRegistrationRequestDto finishRegistrationRequestDto) {
        Optional<Client> client = clientRepository.findById(id);
        client.ifPresent(cl -> {
            cl.setGender(finishRegistrationRequestDto.getGender());
            cl.setMaritalStatus(finishRegistrationRequestDto.getMaritalStatus());
            cl.setDependentAmount(finishRegistrationRequestDto.getDependentAmount());
            cl.setEmploymentId(empMapper.employmentDtoToEmployment(finishRegistrationRequestDto.getEmployment()));
            cl.setAccount(finishRegistrationRequestDto.getAccount());
        });
        log.info("Client id={} updated");
    }

}
