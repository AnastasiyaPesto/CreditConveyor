package ru.zentsova.application.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.zentsova.application.clients.deal.DealClient;
import ru.zentsova.application.model.LoanApplicationRequestDto;
import ru.zentsova.application.model.LoanOfferDto;
import ru.zentsova.application.util.validator.LoanApplicationRequestDtoValidator;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealServiceImplTest {

    @Mock
    private DealClient dealClient;
    @Mock
    private LoanApplicationRequestDtoValidator validator;
    @InjectMocks
    private DealServiceImpl dealService;

    @Test
    public void testExecuteGetAllPossibleOffers_returnOk_whenValidationIsSuccessful() {
        List<LoanOfferDto> offersExpected = Arrays.asList(
                new LoanOfferDto().applicationId(4L).rate(new BigDecimal("1.00")),
                new LoanOfferDto().applicationId(2L).rate(new BigDecimal("2.00")),
                new LoanOfferDto().applicationId(3L).rate(new BigDecimal("1.30")),
                new LoanOfferDto().applicationId(1L).rate(new BigDecimal("0.30"))
        );
        LoanApplicationRequestDto dto = new LoanApplicationRequestDto();

        when(validator.validate(dto, true)).thenReturn(true);
        when(dealClient.getAllPossibleOffers(dto)).thenReturn(new ResponseEntity<>(offersExpected, HttpStatus.OK));

        ResponseEntity<List<LoanOfferDto>> response = dealService.getAllPossibleOffers(dto);

        verify(dealClient, times(1)).getAllPossibleOffers(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(offersExpected.size(), response.getBody().size());
    }

    @Test
    public void testExecuteGetAllPossibleOffers_returnBad_whenValidationIsFailed() {
        LoanApplicationRequestDto dto = new LoanApplicationRequestDto();

        when(validator.validate(dto, true)).thenReturn(false);

        ResponseEntity<List<LoanOfferDto>> response = dealService.getAllPossibleOffers(dto);

        verify(dealClient, times(0)).getAllPossibleOffers(dto);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testExecuteChooseOneOffer() {
        LoanOfferDto dto = new LoanOfferDto();

        dealService.chooseOneOffer(dto);

        verify(dealClient, times(1)).chooseOneOffer(dto);
    }

}