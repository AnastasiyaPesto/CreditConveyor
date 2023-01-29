package ru.zentsova.deal.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zentsova.deal.model.Credit;
import ru.zentsova.deal.model.CreditStatus;
import ru.zentsova.deal.repositories.CreditRepository;
import ru.zentsova.deal.services.CreditService;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditServiceImplTest {

    @Mock
    CreditRepository creditRepository;

    @InjectMocks
    CreditServiceImpl creditService;

    @Test
    public void testSave() throws NoSuchFieldException, IllegalAccessException {
        Credit credit = new Credit();
        credit.setId(1L);

        setField(creditService, "msgSaved", "{} was saved");
        when(creditRepository.save(ArgumentMatchers.any(Credit.class))).thenReturn(credit);

        Credit creditSaved = creditService.save(credit);

        verify(creditRepository, times(1)).save(ArgumentMatchers.any(Credit.class));

        assertEquals(1, creditSaved.getId());
        assertEquals(CreditStatus.CALCULATED, creditSaved.getCreditStatus());
    }

    private void setField(CreditService service, String filedName, Object fieldValue) throws IllegalAccessException, NoSuchFieldException {
        Field field = service.getClass().getDeclaredField(filedName);
        field.setAccessible(true);
        field.set(service, fieldValue);
    }
}