package ru.zentsova.deal.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zentsova.deal.exceptions.EntityNotExistException;
import ru.zentsova.deal.model.*;
import ru.zentsova.deal.repositories.ApplicationRepository;
import ru.zentsova.deal.services.ApplicationService;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock
    ApplicationRepository applicationRepository;

    @InjectMocks
    ApplicationServiceImpl applicationService;

    @Test
    public void testCreateAndSaveNewApplication() {
        LocalDateTime timeStatus = LocalDateTime.of(2023, 1, 1, 12, 0);

        Client client = new Client();
        client.setFirstName("Test");

        Application savedApplicationRequired = new Application();
        savedApplicationRequired.setId(1L);
        savedApplicationRequired.setStatus(ApplicationStatus.PREAPPROVAL);
        ApplicationStatusHistoryDto statusHistory = new ApplicationStatusHistoryDto()
                .status(ApplicationStatus.PREAPPROVAL.name())
                .time(timeStatus.toString())
                .changeType(ApplicationStatusHistoryDto.ChangeTypeEnum.AUTOMATIC);
        List<ApplicationStatusHistoryDto> statusHistoryList = new ArrayList<>(Collections.singletonList(statusHistory));
        savedApplicationRequired.setStatusHistory(statusHistoryList);
        savedApplicationRequired.setClient(client);

        when(applicationRepository.save(ArgumentMatchers.any(Application.class))).thenReturn(savedApplicationRequired);

        Application applicationSavedActual = applicationService.createAndSaveNewApplication(client);

        verify(applicationRepository, times(1)).save(ArgumentMatchers.any(Application.class));

        assertEquals(1, applicationSavedActual.getId());
        assertEquals(ApplicationStatus.PREAPPROVAL, applicationSavedActual.getStatus());
        assertArrayEquals(statusHistoryList.toArray(), savedApplicationRequired.getStatusHistory().toArray());
    }

    @Test
    public void testUpdateApplication_setAppliedOffer() {
        Application application = new Application();
        application.setId(1L);

        AppliedOffer appliedOffer = new AppliedOffer();
        appliedOffer.setApplicationId(1);

        assertNull(application.getAppliedOffer());
        applicationService.update(application, appliedOffer);
        assertNotNull(application.getAppliedOffer());
        assertEquals(1, application.getAppliedOffer().getApplicationId());
    }

    @Test
    public void testUpdateApplication_setCreditAndStatus() {
        Clock clock = Clock.fixed(Instant.parse("2023-01-01T10:00:00.00Z"), ZoneId.of("UTC"));
        LocalDateTime timeMock = LocalDateTime.now(clock);

        Application application = new Application();
        application.setId(1L);

        Credit credit = new Credit();
        credit.setId(11L);

        ApplicationStatusHistoryDto statusHistory = new ApplicationStatusHistoryDto()
                .status(ApplicationStatus.APPROVED.name())
                .time(timeMock.toString())
                .changeType(ApplicationStatusHistoryDto.ChangeTypeEnum.AUTOMATIC);
        List<ApplicationStatusHistoryDto> statusHistoryList = new ArrayList<>(Collections.singletonList(statusHistory));

        assertNull(application.getCredit());
        assertNotEquals(ApplicationStatus.APPROVED, application.getStatus());
        assertNull(application.getStatusHistory());

        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(timeMock);

            applicationService.update(application, credit, ApplicationStatus.APPROVED, ApplicationStatusHistoryDto.ChangeTypeEnum.AUTOMATIC);
            assertNotNull(application.getCredit());
            assertNotNull(application.getStatusHistory());
            assertArrayEquals(statusHistoryList.toArray(), application.getStatusHistory().toArray());
        }
    }

    @Test
    public void testFindById_returnApplication_whenIdIsExist() {
        Application application = new Application();
        application.setId(1L);
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        Application applicationFromDB = applicationService.findById(1L);

        assertEquals(1, applicationFromDB.getId());
    }

    @Test
    public void testFindById_shouldExceptionThrown_whenIdIsNotExist() throws NoSuchFieldException, IllegalAccessException {
        setField(applicationService, "applicationNotFound", "Application wasn't found with id=%s");
        when(applicationRepository.findById(1L)).thenReturn(Optional.empty());


        Exception ex = assertThrows(EntityNotExistException.class, () -> {
            Application applicationFromDB = applicationService.findById(1L);
            assertEquals(1, applicationFromDB.getId());
        });

        String expectedMessage = "Application wasn't found with id=1";
        assertEquals(expectedMessage, ex.getMessage());
    }

    private void setField(ApplicationService service, String filedName, Object fieldValue) throws IllegalAccessException, NoSuchFieldException {
        Field field = service.getClass().getDeclaredField(filedName);
        field.setAccessible(true);
        field.set(service, fieldValue);
    }
}