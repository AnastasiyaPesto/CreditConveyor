package ru.zentsova.deal.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zentsova.deal.model.Client;
import ru.zentsova.deal.model.Passport;
import ru.zentsova.deal.repositories.ClientRepository;
import ru.zentsova.deal.services.ClientService;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    ClientRepository clientRepository;

    @InjectMocks
    ClientServiceImpl clientService;

    @Test
    public void testSave() throws NoSuchFieldException, IllegalAccessException {
        Passport passport = new Passport();
        passport.setPassportSeries("1111");
        passport.setPassportNumber("111111");

        Client client = new Client();
        client.setId(1L);

        setField(clientService, "msgSaved", "{} was saved");
        when(clientRepository.save(ArgumentMatchers.any(Client.class))).thenReturn(client);

        Client clientSaved = clientService.save(client, passport);

        verify(clientRepository, times(1)).save(ArgumentMatchers.any(Client.class));

        assertEquals(1, clientSaved.getId());
        assertEquals("1111", clientSaved.getPassportId().getPassportSeries());
        assertEquals("111111", clientSaved.getPassportId().getPassportNumber());
    }

    private void setField(ClientService service, String filedName, Object fieldValue) throws IllegalAccessException, NoSuchFieldException {
        Field field = service.getClass().getDeclaredField(filedName);
        field.setAccessible(true);
        field.set(service, fieldValue);
    }
}