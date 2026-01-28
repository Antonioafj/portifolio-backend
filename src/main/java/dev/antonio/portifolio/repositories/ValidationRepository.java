package dev.antonio.portifolio.repositories;

import dev.antonio.portifolio.entities.ValidationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ValidationRepository extends JpaRepository<ValidationEntity, Long> {

    Optional<ValidationEntity> findByDestination(String destination);

    void deleteByDestination(String destination);
}
