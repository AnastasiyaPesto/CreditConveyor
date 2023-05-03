package ru.zentsova.deal.mappers;

import org.mapstruct.Mapper;
import ru.zentsova.deal.model.Employment;
import ru.zentsova.deal.model.EmploymentDto;

@Mapper
public interface EmploymentMapper {
    Employment employmentDtoToEmployment(EmploymentDto dto);
}
