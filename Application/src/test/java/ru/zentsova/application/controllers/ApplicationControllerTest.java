package ru.zentsova.application.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.zentsova.application.model.LoanApplicationRequestDto;
import ru.zentsova.application.model.LoanOfferDto;
import ru.zentsova.application.services.DealService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApplicationControllerTest {

    @Mock
    private DealService dealService;
    @InjectMocks
    private ApplicationController controller;

    @Test
    public void testCalculateAllPossibleLoanOffers() {
        List<LoanOfferDto> offersExpected = Arrays.asList(
                new LoanOfferDto().applicationId(4L).rate(new BigDecimal("1.00")),
                new LoanOfferDto().applicationId(2L).rate(new BigDecimal("2.00")),
                new LoanOfferDto().applicationId(3L).rate(new BigDecimal("1.30")),
                new LoanOfferDto().applicationId(1L).rate(new BigDecimal("0.30"))
        );
        LoanApplicationRequestDto dto = new LoanApplicationRequestDto();

        Mockito.when(dealService.executeGetAllPossibleOffers(dto)).thenReturn(new ResponseEntity<>(offersExpected, HttpStatus.OK));

        ResponseEntity<List<LoanOfferDto>> response = controller.calculateAllPossibleLoanOffers(dto);

        verify(dealService, times(1)).executeGetAllPossibleOffers(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(offersExpected.size(), response.getBody().size());
    }

    @Test
    public void testChooseOneOffer() {
        LoanOfferDto dto = new LoanOfferDto();

        controller.chooseOneOffer(dto);

        verify(dealService, times(1)).executeChooseOneOffer(dto);
    }

}