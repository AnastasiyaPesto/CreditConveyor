package ru.zentsova.deal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.zentsova.deal.model.Credit;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {
}
