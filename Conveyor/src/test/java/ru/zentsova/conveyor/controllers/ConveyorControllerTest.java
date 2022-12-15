package ru.zentsova.conveyor.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.zentsova.conveyor.model.CreditDto;
import ru.zentsova.conveyor.model.LoanApplicationRequestDto;
import ru.zentsova.conveyor.model.LoanOfferDto;
import ru.zentsova.conveyor.model.ScoringDataDto;
import ru.zentsova.conveyor.services.impl.ConveyorServiceImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Class ConveyorControllerTest
 */
@ExtendWith(MockitoExtension.class)
class ConveyorControllerTest {

    @Mock
    ConveyorServiceImpl conveyorService;

    @InjectMocks
    ConveyorController conveyorController;

    @Test
    void testGetAllPossibleOffers_shouldReturnOk() {
        LoanApplicationRequestDto loanAppReqDtoMock = Mockito.mock(LoanApplicationRequestDto.class);
        List<LoanOfferDto> offers = List.of(new LoanOfferDto().applicationId(1L), new LoanOfferDto().applicationId(2L));
        when(conveyorService.getAllPossibleOffers(loanAppReqDtoMock)).thenReturn(offers);

        ResponseEntity<List<LoanOfferDto>> response = conveyorController.getAllPossibleOffers(loanAppReqDtoMock);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(1, response.getBody().get(0).getApplicationId());
        assertEquals(2, response.getBody().get(1).getApplicationId());
    }

    @Test
    void testGetFullCalculatedParameters_shouldReturnOk() {
        CreditDto creditDto = new CreditDto().amount(new BigDecimal("300000.00"));
        ScoringDataDto scoringDataDtoMock = Mockito.mock(ScoringDataDto.class);
        when(conveyorService.getLoanConditions(scoringDataDtoMock)).thenReturn(creditDto);

        ResponseEntity<CreditDto> response = conveyorController.getFullCalculatedParameters(scoringDataDtoMock);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(new BigDecimal("300000.00"), response.getBody().getAmount());
    }
}