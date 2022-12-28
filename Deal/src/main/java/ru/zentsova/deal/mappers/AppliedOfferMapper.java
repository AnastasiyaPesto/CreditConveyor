package ru.zentsova.deal.mappers;

import org.mapstruct.Mapper;
import ru.zentsova.deal.model.AppliedOffer;
import ru.zentsova.deal.model.LoanOfferDto;

@Mapper
public interface AppliedOfferMapper {

    AppliedOffer loanOfferDtoToAppliedOffer(LoanOfferDto dto);
}
