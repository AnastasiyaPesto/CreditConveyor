package ru.zentsova.deal.mappers;

import org.mapstruct.Mapper;
import ru.zentsova.deal.model.Credit;
import ru.zentsova.deal.model.CreditDto;

@Mapper
public interface CreditMapper {

    Credit creditDtoToCredit(CreditDto dto);
}