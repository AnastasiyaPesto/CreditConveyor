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
import ru.zentsova.deal.utils.ConveyorServiceUtils;

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
    private final ConveyorServiceUtils conveyorServiceUtils;

    @Override
    public ResponseEntity<Void> chooseOneOffer(LoanOfferDto loanOfferDto) {
        applicationService.update(applicationService.findById(loanOfferDto.getApplicationId()), appliedOfferMapper.loanOfferDtoToAppliedOffer(loanOfferDto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> finishRegistrationAndFullCalculation(Long applicationId, FinishRegistrationRequestDto finishRegistrationRequestDto) {
        ScoringDataDto scoringDataDto = new ScoringDataDto();
        Application application = applicationService.findById(applicationId);
        conveyorServiceUtils.enrichScoringDataDto(scoringDataDto, finishRegistrationRequestDto, application);

        CreditDto creditDto = conveyorServiceClient.getFullCalculatedParameters(scoringDataDto).getBody();
        Credit createdCredit = creditService.save(creditMapper.creditDtoToCredit(creditDto));
        applicationService.update(application, createdCredit, ApplicationStatus.APPROVED, ApplicationStatusHistoryDto.ChangeTypeEnum.AUTOMATIC);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<LoanOfferDto>> getAllPossibleOffers(LoanApplicationRequestDto loanApplicationRequestDto) {
        Client client = clientMapper.loanApplicationRequestDtoToClient(loanApplicationRequestDto);
        Passport passport = passportMapper.loanApplicationRequestDtoToPassport(loanApplicationRequestDto);
        Application createdApplication = applicationService.createAndSaveNewApplication(clientService.save(client, passport));

        ResponseEntity<List<LoanOfferDto>> offersResponse = conveyorServiceClient.getAllPossibleOffers(loanApplicationRequestDto);
        conveyorServiceUtils.setApplicationIdToOffers(offersResponse.getBody(), createdApplication.getId());
        List<LoanOfferDto> offers = conveyorServiceUtils.sort(offersResponse.getBody(), Comparator.comparing(LoanOfferDto::getRate).reversed());

        return new ResponseEntity<>(offers, HttpStatus.OK);
    }
}
