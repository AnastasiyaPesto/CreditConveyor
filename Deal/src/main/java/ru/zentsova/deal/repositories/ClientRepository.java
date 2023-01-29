package ru.zentsova.deal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.zentsova.deal.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}
