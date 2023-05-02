package ru.zentsova.deal.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.zentsova.deal.api.DealApi;
import ru.zentsova.deal.feign.clients.ConveyorServiceClient;
import ru.zentsova.deal.mappers.AppliedOfferMapper;
import ru.zentsova.deal.mappers.ClientMapper;
import ru.zentsova.deal.mappers.CreditMapper;
import ru.zentsova.deal.mappers.PassportMapper;
import ru.zentsova.deal.model.*;
import ru.zentsova.deal.services.ApplicationService;
import ru.zentsova.deal.services.ClientService;
import ru.zentsova.deal.services.CreditService;
import ru.zentsova.deal.services.KafkaProducerService;
import ru.zentsova.deal.utils.ConveyorServiceHelper;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class DealController implements DealApi {

    private final ClientMapper clientMapper;
    private final CreditMapper creditMapper;
    private final PassportMapper passportMapper;
    private final AppliedOfferMapper appliedOfferMapper;

    private final ClientService clientService;
    private final CreditService creditService;
    private final ApplicationService applicationService;

    private final ConveyorServiceClient conveyorServiceClient;
    private final ConveyorServiceHelper conveyorServiceHelper;

    private final KafkaProducerService producerService;

    @Override
    public ResponseEntity<Void> chooseOneOffer(LoanOfferDto loanOfferDto) {
        Application application = applicationService.findById(loanOfferDto.getApplicationId());
        applicationService.update(application, appliedOfferMapper.loanOfferDtoToAppliedOffer(loanOfferDto));
        producerService.sendFinishRegistrationEvent(loanOfferDto.getApplicationId());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> finishRegistrationAndFullCalculation(Long applicationId, FinishRegistrationRequestDto finishRegistrationRequestDto) {
        ScoringDataDto scoringDataDto = new ScoringDataDto();
        Application application = applicationService.findById(applicationId);
        conveyorServiceHelper.populateScoringDataDto(scoringDataDto, finishRegistrationRequestDto, application);

        CreditDto creditDto = conveyorServiceClient.getFullCalculatedParameters(scoringDataDto).getBody();
        Credit createdCredit = creditService.save(creditMapper.creditDtoToCredit(creditDto));
        // todo: update client???? (sequence-диаграмма)
        applicationService.update(application, createdCredit, ApplicationStatus.CC_APPROVED, ApplicationStatusHistoryDto.ChangeTypeEnum.AUTOMATIC);
        producerService.sendCreateDocumentsEvent(applicationId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<LoanOfferDto>> getAllPossibleOffers(LoanApplicationRequestDto loanApplicationRequestDto) {
        Client client = clientMapper.loanApplicationRequestDtoToClient(loanApplicationRequestDto);
        Passport passport = passportMapper.loanApplicationRequestDtoToPassport(loanApplicationRequestDto);
        Client clientSaved = clientService.save(client, passport);
        Application createdApplication = applicationService.createAndSaveNewApplication(clientSaved);

        ResponseEntity<List<LoanOfferDto>> offersResponse = conveyorServiceClient.getAllPossibleOffers(loanApplicationRequestDto);
        conveyorServiceHelper.setApplicationIdToOffers(offersResponse.getBody(), createdApplication.getId());
        List<LoanOfferDto> offers = conveyorServiceHelper.sort(offersResponse.getBody(), Comparator.comparing(LoanOfferDto::getRate).reversed());

        return new ResponseEntity<>(offers, HttpStatus.OK);
    }
}
