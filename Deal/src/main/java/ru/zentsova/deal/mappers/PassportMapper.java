package ru.zentsova.deal.mappers;

import org.mapstruct.Mapper;
import ru.zentsova.deal.model.LoanApplicationRequestDto;
import ru.zentsova.deal.model.Passport;

@Mapper
public interface PassportMapper {

    Passport loanApplicationRequestDtoToPassport(LoanApplicationRequestDto dto);
}
