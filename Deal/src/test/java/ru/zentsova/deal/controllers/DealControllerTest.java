package ru.zentsova.deal.controllers;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.zentsova.deal.feign.clients.ConveyorServiceClient;
import ru.zentsova.deal.mappers.AppliedOfferMapper;
import ru.zentsova.deal.mappers.ClientMapper;
import ru.zentsova.deal.mappers.CreditMapper;
import ru.zentsova.deal.mappers.PassportMapper;
import ru.zentsova.deal.model.*;
import ru.zentsova.deal.services.ApplicationService;
import ru.zentsova.deal.services.ClientService;
import ru.zentsova.deal.services.CreditService;
import ru.zentsova.deal.utils.ConveyorServiceHelper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealControllerTest {

    @Mock
    private ApplicationService applicationService;
    @Mock
    private CreditService creditService;
    @Mock
    private ClientService clientService;
    @Mock
    private ConveyorServiceHelper conveyorServiceHelper;
    @Mock
    private ConveyorServiceClient conveyorServiceClient;
    @Mock
    private AppliedOfferMapper appliedOfferMapper;
    @Mock
    private CreditMapper creditMapper;
    @Mock
    private ClientMapper clientMapper;
    @Mock
    private PassportMapper passportMapper;

    @InjectMocks
    private DealController controller;

//    @Test
    public void testChooseOneOffer() {
        LoanOfferDto dto = new LoanOfferDto().applicationId(1L);
        Application application = new Application();
        when(applicationService.findById(1L)).thenReturn(application);

        ResponseEntity<Void> response = controller.chooseOneOffer(dto);

        verify(applicationService, times(1)).findById(1L);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    }

//    @Test
    public void testFinishRegistrationAndFullCalculation() {
        Application application = new Application();
        CreditDto creditDto = new CreditDto();
        Credit credit = new Credit();
        Credit creditSaved = new Credit();

        when(applicationService.findById(1L)).thenReturn(application);
        when(conveyorServiceClient.getFullCalculatedParameters(any(ScoringDataDto.class))).thenReturn(new ResponseEntity<>(creditDto, HttpStatus.OK));
        when(creditService.save(credit)).thenReturn(creditSaved);
        when(creditMapper.creditDtoToCredit(any(CreditDto.class))).thenReturn(credit);
        doNothing().when(applicationService).update(application, creditSaved, ApplicationStatus.APPROVED, ApplicationStatusHistoryDto.ChangeTypeEnum.AUTOMATIC);

        ResponseEntity<Void> response = controller.finishRegistrationAndFullCalculation(1L, new FinishRegistrationRequestDto());

        verify(applicationService, times(1)).findById(1L);
        verify(conveyorServiceClient, times(1)).getFullCalculatedParameters(any(ScoringDataDto.class));
        verify(creditMapper, times(1)).creditDtoToCredit(any(CreditDto.class));
        verify(creditService, times(1)).save(credit);
        verify(applicationService, times(1)).update(application, creditSaved, ApplicationStatus.APPROVED, ApplicationStatusHistoryDto.ChangeTypeEnum.AUTOMATIC);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    }

//    @Test
    public void testGetAllPossibleOffers() {
        List<LoanOfferDto> offers = Arrays.asList(
                new LoanOfferDto().applicationId(4L).rate(new BigDecimal("1.00")),
                new LoanOfferDto().applicationId(2L).rate(new BigDecimal("2.00")),
                new LoanOfferDto().applicationId(3L).rate(new BigDecimal("1.30")),
                new LoanOfferDto().applicationId(1L).rate(new BigDecimal("0.30"))
        );
        LoanApplicationRequestDto loanApplicationRequestDto = new LoanApplicationRequestDto();
        Client client = new Client();
        Client savedClient = new Client();
        Passport passport = new Passport();
        Application createdApplication = new Application();
        long applicationId = 1L;
        createdApplication.setId(applicationId);

        when(clientMapper.loanApplicationRequestDtoToClient(loanApplicationRequestDto)).thenReturn(client);
        when(passportMapper.loanApplicationRequestDtoToPassport(loanApplicationRequestDto)).thenReturn(passport);
        when(clientService.save(client, passport)).thenReturn(savedClient);
        when(applicationService.createAndSaveNewApplication(savedClient)).thenReturn(createdApplication);
        when(conveyorServiceClient.getAllPossibleOffers(loanApplicationRequestDto)).thenReturn(new ResponseEntity<>(offers, HttpStatus.OK));
        doNothing().when(conveyorServiceHelper).setApplicationIdToOffers(offers, applicationId);
        lenient().when(conveyorServiceHelper.sort(offers, Comparator.comparing(LoanOfferDto::getRate).reversed())).thenReturn(offers);

        ResponseEntity<List<LoanOfferDto>> allPossibleOffers = controller.getAllPossibleOffers(loanApplicationRequestDto);

        verify(clientMapper, times(1)).loanApplicationRequestDtoToClient(loanApplicationRequestDto);
        verify(passportMapper, times(1)).loanApplicationRequestDtoToPassport(loanApplicationRequestDto);
        verify(clientService, times(1)).save(client, passport);
        verify(applicationService, times(1)).createAndSaveNewApplication(savedClient);
        verify(conveyorServiceClient, times(1)).getAllPossibleOffers(loanApplicationRequestDto);
        verify(conveyorServiceHelper, times(1)).setApplicationIdToOffers(offers, applicationId);

        assertEquals(HttpStatus.OK.value(), allPossibleOffers.getStatusCode().value());
    }
}