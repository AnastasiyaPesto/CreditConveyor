package ru.zentsova.deal.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zentsova.deal.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConveyorServiceHelperTest {

    ConveyorServiceHelper helper;

    @BeforeEach
    public void init() {
        helper = new ConveyorServiceHelper();
    }

    @Test
    void testSetApplicationIdToOffers_shouldSetSameIdToEachOffer() {
        List<LoanOfferDto> offers = new ArrayList<>();
        offers.add(new LoanOfferDto().applicationId(1L));
        offers.add(new LoanOfferDto().applicationId(2L));
        offers.add(new LoanOfferDto().applicationId(3L));
        offers.add(new LoanOfferDto().applicationId(4L));

        helper.setApplicationIdToOffers(offers, 100L);

        assertNotNull(offers);
        assertEquals(4, offers.size());
        assertEquals(100L, offers.get(0).getApplicationId());
        assertEquals(100L, offers.get(1).getApplicationId());
        assertEquals(100L, offers.get(2).getApplicationId());
        assertEquals(100L, offers.get(3).getApplicationId());

    }

    @Test
    void testSort_returnEmptyList_ifOffersIsNull() {
        assertEquals(0, helper.sort(null, Comparator.comparing(LoanOfferDto::getRate)).size());
    }

    @Test
    void testSort_returnOffersSortedInNaturalOrderByApplicationId() {
        List<LoanOfferDto> offers = new ArrayList<>();
        offers.add(new LoanOfferDto().applicationId(3L));
        offers.add(new LoanOfferDto().applicationId(4L));
        offers.add(new LoanOfferDto().applicationId(1L));
        offers.add(new LoanOfferDto().applicationId(2L));

        List<LoanOfferDto> sortedOffers = helper.sort(offers, Comparator.comparing(LoanOfferDto::getApplicationId));

        assertEquals(4, sortedOffers.size());
        assertEquals(1L, sortedOffers.get(0).getApplicationId());
        assertEquals(2L, sortedOffers.get(1).getApplicationId());
        assertEquals(3L, sortedOffers.get(2).getApplicationId());
        assertEquals(4L, sortedOffers.get(3).getApplicationId());
    }

    @Test
    void testSort_returnOffersSortedInReverseOrderByApplicationId() {
        List<LoanOfferDto> offers = new ArrayList<>();
        offers.add(new LoanOfferDto().applicationId(3L));
        offers.add(new LoanOfferDto().applicationId(4L));
        offers.add(new LoanOfferDto().applicationId(1L));
        offers.add(new LoanOfferDto().applicationId(2L));

        List<LoanOfferDto> sortedOffers = helper.sort(offers, Comparator.comparing(LoanOfferDto::getApplicationId).reversed());

        assertEquals(4, sortedOffers.size());
        assertEquals(4L, sortedOffers.get(0).getApplicationId());
        assertEquals(3L, sortedOffers.get(1).getApplicationId());
        assertEquals(2L, sortedOffers.get(2).getApplicationId());
        assertEquals(1L, sortedOffers.get(3).getApplicationId());
    }

    @Test
    void testEnrichScoringDataDto_whenClientIsNull() {
        ScoringDataDto scoringDataDtoSpy = spy(ScoringDataDto.class);

        FinishRegistrationRequestDto finishDtoMock = mock(FinishRegistrationRequestDto.class);
        Application applicationMock = mock(Application.class);
        AppliedOffer appliedOfferMock = mock(AppliedOffer.class);
        EmploymentDto employmentDtoMock = mock(EmploymentDto.class);
        Client client = null;
        LocalDate now = LocalDate.now();

        when(applicationMock.getClient()).thenReturn(client);

        when(applicationMock.getAppliedOffer()).thenReturn(appliedOfferMock);
        when(appliedOfferMock.getTotalAmount()).thenReturn(new BigDecimal("100.00"));
        when(appliedOfferMock.getTerm()).thenReturn(120);
        when(appliedOfferMock.isInsuranceEnabled()).thenReturn(true);
        when(appliedOfferMock.isSalaryClient()).thenReturn(true);

        when(finishDtoMock.getGender()).thenReturn(Gender.MALE);
        when(finishDtoMock.getMaritalStatus()).thenReturn(MaritalStatus.SINGLE);
        when(finishDtoMock.getDependentAmount()).thenReturn(1);
        when(finishDtoMock.getEmployment()).thenReturn(employmentDtoMock);
        when(finishDtoMock.getAccount()).thenReturn("123");
        when(finishDtoMock.getPassportIssueDate()).thenReturn(now);
        when(finishDtoMock.getPassportIssueBranch()).thenReturn("100-001");

        helper.populateScoringDataDto(scoringDataDtoSpy, finishDtoMock, applicationMock);

        verify(finishDtoMock, times(1)).getGender();
        verify(finishDtoMock, times(1)).getMaritalStatus();
        verify(finishDtoMock, times(1)).getDependentAmount();
        verify(finishDtoMock, times(1)).getEmployment();
        verify(finishDtoMock, times(1)).getAccount();
        verify(finishDtoMock, times(1)).getPassportIssueBranch();
        verify(finishDtoMock, times(1)).getPassportIssueDate();

        verify(applicationMock, times(4)).getAppliedOffer();
        verify(applicationMock, times(1)).getClient();

        verify(appliedOfferMock, times(1)).getTotalAmount();
        verify(appliedOfferMock, times(1)).getTerm();
        verify(appliedOfferMock, times(1)).isInsuranceEnabled();
        verify(appliedOfferMock, times(1)).isSalaryClient();

        verify(scoringDataDtoSpy, times(1)).gender(Gender.MALE);
        verify(scoringDataDtoSpy, times(1)).maritalStatus(MaritalStatus.SINGLE);
        verify(scoringDataDtoSpy, times(1)).dependentAmount(1);
        verify(scoringDataDtoSpy, times(1)).employment(employmentDtoMock);
        verify(scoringDataDtoSpy, times(1)).account("123");
        verify(scoringDataDtoSpy, times(1)).passportIssueDate(now);
        verify(scoringDataDtoSpy, times(1)).passportIssueBranch("100-001");
        verify(scoringDataDtoSpy, times(1)).amount(new BigDecimal("100.00"));
        verify(scoringDataDtoSpy, times(1)).term(120);
        verify(scoringDataDtoSpy, times(1)).isInsuranceEnabled(true);
        verify(scoringDataDtoSpy, times(1)).isSalaryClient(true);
        verify(scoringDataDtoSpy, never()).firstName(anyString());
        verify(scoringDataDtoSpy, never()).lastName(anyString());
        verify(scoringDataDtoSpy, never()).middleName(anyString());
        verify(scoringDataDtoSpy, never()).birthdate(any(LocalDate.class));
        verify(scoringDataDtoSpy, never()).passportSeries(anyString());
        verify(scoringDataDtoSpy, never()).passportNumber(anyString());
    }

    @Test
    void testEnrichScoringDataDto_whenClientIsNotNull() {
        ScoringDataDto scoringDataDtoSpy = spy(ScoringDataDto.class);

        FinishRegistrationRequestDto finishDtoMock = mock(FinishRegistrationRequestDto.class);
        Application applicationMock = mock(Application.class);
        AppliedOffer appliedOfferMock = mock(AppliedOffer.class);
        Client clientMock = mock(Client.class);
        EmploymentDto employmentDtoMock = mock(EmploymentDto.class);
        Passport passportMock = mock(Passport.class);
        LocalDate now = LocalDate.now();

        when(applicationMock.getClient()).thenReturn(clientMock);
        when(clientMock.getFirstName()).thenReturn("firstName");
        when(clientMock.getLastName()).thenReturn("lastName");
        when(clientMock.getMiddleName()).thenReturn(null);
        when(clientMock.getBirthdate()).thenReturn(now);
        when(clientMock.getPassportId()).thenReturn(passportMock);

        when(passportMock.getPassportSeries()).thenReturn("1111");
        when(passportMock.getPassportNumber()).thenReturn("111111");

        when(applicationMock.getAppliedOffer()).thenReturn(appliedOfferMock);
        when(appliedOfferMock.getTotalAmount()).thenReturn(new BigDecimal("100.00"));
        when(appliedOfferMock.getTerm()).thenReturn(120);
        when(appliedOfferMock.isInsuranceEnabled()).thenReturn(true);
        when(appliedOfferMock.isSalaryClient()).thenReturn(true);

        when(finishDtoMock.getGender()).thenReturn(Gender.MALE);
        when(finishDtoMock.getMaritalStatus()).thenReturn(MaritalStatus.SINGLE);
        when(finishDtoMock.getDependentAmount()).thenReturn(1);
        when(finishDtoMock.getEmployment()).thenReturn(employmentDtoMock);
        when(finishDtoMock.getAccount()).thenReturn("123");
        when(finishDtoMock.getPassportIssueDate()).thenReturn(now);
        when(finishDtoMock.getPassportIssueBranch()).thenReturn("100-001");

        helper.populateScoringDataDto(scoringDataDtoSpy, finishDtoMock, applicationMock);

        verify(finishDtoMock, times(1)).getGender();
        verify(finishDtoMock, times(1)).getMaritalStatus();
        verify(finishDtoMock, times(1)).getDependentAmount();
        verify(finishDtoMock, times(1)).getEmployment();
        verify(finishDtoMock, times(1)).getAccount();
        verify(finishDtoMock, times(1)).getPassportIssueBranch();
        verify(finishDtoMock, times(1)).getPassportIssueDate();

        verify(applicationMock, times(4)).getAppliedOffer();
        verify(applicationMock, times(1)).getClient();

        verify(appliedOfferMock, times(1)).getTotalAmount();
        verify(appliedOfferMock, times(1)).getTerm();
        verify(appliedOfferMock, times(1)).isInsuranceEnabled();
        verify(appliedOfferMock, times(1)).isSalaryClient();

        verify(scoringDataDtoSpy, times(1)).gender(Gender.MALE);
        verify(scoringDataDtoSpy, times(1)).maritalStatus(MaritalStatus.SINGLE);
        verify(scoringDataDtoSpy, times(1)).dependentAmount(1);
        verify(scoringDataDtoSpy, times(1)).employment(employmentDtoMock);
        verify(scoringDataDtoSpy, times(1)).account("123");
        verify(scoringDataDtoSpy, times(1)).passportIssueDate(now);
        verify(scoringDataDtoSpy, times(1)).passportIssueBranch("100-001");
        verify(scoringDataDtoSpy, times(1)).amount(new BigDecimal("100.00"));
        verify(scoringDataDtoSpy, times(1)).term(120);
        verify(scoringDataDtoSpy, times(1)).isInsuranceEnabled(true);
        verify(scoringDataDtoSpy, times(1)).isSalaryClient(true);
        verify(scoringDataDtoSpy, times(1)).firstName("firstName");
        verify(scoringDataDtoSpy, times(1)).lastName("lastName");
        verify(scoringDataDtoSpy, times(1)).birthdate(now);
        verify(scoringDataDtoSpy, times(1)).passportSeries("1111");
        verify(scoringDataDtoSpy, times(1)).passportNumber("111111");
        verify(scoringDataDtoSpy, never()).middleName(anyString());

        when(clientMock.getMiddleName()).thenReturn("middleName");

        helper.populateScoringDataDto(scoringDataDtoSpy, finishDtoMock, applicationMock);

        verify(scoringDataDtoSpy, times(1)).middleName("middleName");
    }
}