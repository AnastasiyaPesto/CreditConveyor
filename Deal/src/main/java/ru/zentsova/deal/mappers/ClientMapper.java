package ru.zentsova.deal.mappers;

import org.mapstruct.Mapper;
import ru.zentsova.deal.model.Client;
import ru.zentsova.deal.model.LoanApplicationRequestDto;

@Mapper
public interface ClientMapper {

    Client loanApplicationRequestDtoToClient(LoanApplicationRequestDto dto);
}
