package ru.zentsova.deal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.zentsova.deal.model.AppliedOffer;

@Repository
public interface AppliedOfferRepository extends JpaRepository<AppliedOffer, Long> {
}