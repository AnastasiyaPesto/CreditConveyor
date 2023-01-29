package ru.zentsova.deal.mappers;

import org.mapstruct.Mapper;
import ru.zentsova.deal.model.Application;
import ru.zentsova.deal.model.LoanOfferDto;

@Mapper
public interface ApplicationMapper {

    Application loanOfferDtoToApplication(LoanOfferDto dto);
}
