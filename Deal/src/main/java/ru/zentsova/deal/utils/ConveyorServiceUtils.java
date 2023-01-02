package ru.zentsova.deal.utils;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zentsova.deal.model.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class ConveyorServiceUtils {

    public void setApplicationIdToOffers(List<LoanOfferDto> offers, long id) {
        if (offers != null)
            offers.forEach(offer -> offer.setApplicationId(id));
    }

    public List<LoanOfferDto> sort(List<LoanOfferDto> offers, Comparator<LoanOfferDto> comparator) {
        if (offers == null)
            return Collections.emptyList();
        return offers.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public void enrichScoringDataDto(ScoringDataDto scoringDataDto, FinishRegistrationRequestDto finishRegistrationRequestDto, Application application) {
        scoringDataDto
                .gender(finishRegistrationRequestDto.getGender())
                .maritalStatus(finishRegistrationRequestDto.getMaritalStatus())
                .dependentAmount(finishRegistrationRequestDto.getDependentAmount())
                .employment(finishRegistrationRequestDto.getEmployment())
                .account(finishRegistrationRequestDto.getAccount())
                .passportIssueDate(finishRegistrationRequestDto.getPassportIssueDate())
                .passportIssueBranch(finishRegistrationRequestDto.getPassportIssueBranch())
                .amount(application.getAppliedOffer().getTotalAmount())
                .term(application.getAppliedOffer().getTerm())
                .isInsuranceEnabled(application.getAppliedOffer().isInsuranceEnabled())
                .isSalaryClient(application.getAppliedOffer().isSalaryClient());
        Client client = application.getClient();
        if (client != null) {
            scoringDataDto
                    .firstName(client.getFirstName())
                    .lastName(client.getLastName())
                    .birthdate(client.getBirthdate())
                    .passportSeries(client.getPassportId().getPassportSeries())
                    .passportNumber(client.getPassportId().getPassportNumber());
            if (client.getMiddleName() != null && !client.getMiddleName().isBlank())
                scoringDataDto.middleName(client.getMiddleName());
        }
    }
}
