package ru.zentsova.Deal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.zentsova.Deal.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}
