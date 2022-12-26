package ru.zentsova.deal.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.zentsova.deal.api.DealApi;
import ru.zentsova.deal.feign.clients.ConveyorServiceClient;
import ru.zentsova.deal.mappers.ClientMapper;
import ru.zentsova.deal.mappers.PassportMapper;
import ru.zentsova.deal.model.*;
import ru.zentsova.deal.services.ApplicationService;
import ru.zentsova.deal.services.ClientService;
import ru.zentsova.deal.services.ConveyorService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class DealController implements DealApi {

    private final ClientMapper clientMapper;
    private final PassportMapper passportMapper;
    private final ClientService clientService;
    private final ApplicationService applicationService;
    private final ConveyorServiceClient conveyorServiceClient;
    private final ConveyorService conveyorService;

    @Override
    public ResponseEntity<Void> chooseOneOffer(LoanOfferDto loanOfferDto) {
        return DealApi.super.chooseOneOffer(loanOfferDto);
    }

    @Override
    public ResponseEntity<Void> finishRegistrationAndFullCalculation(Long applicationId, FinishRegistrationRequestDto finishRegistrationRequestDto) {
        return DealApi.super.finishRegistrationAndFullCalculation(applicationId, finishRegistrationRequestDto);
    }

    @Override
    public ResponseEntity<List<LoanOfferDto>> getAllPossibleOffers(LoanApplicationRequestDto loanApplicationRequestDto) {
        Client client = clientMapper.loanApplicationRequestDtoToClient(loanApplicationRequestDto);
        Passport passport = passportMapper.loanOfferDtoToPassport(loanApplicationRequestDto);
        Application createdApplication = applicationService.createApplicationAndSave(clientService.save(client, passport));

        ResponseEntity<List<LoanOfferDto>> offersResponse = conveyorServiceClient.getAllPossibleOffers(loanApplicationRequestDto);
        conveyorService.setApplicationId(offersResponse.getBody(), createdApplication.getId());
        List<LoanOfferDto> offers = conveyorService.sortByRate(offersResponse.getBody());

        return new ResponseEntity<>(offers, HttpStatus.OK);
    }
}
