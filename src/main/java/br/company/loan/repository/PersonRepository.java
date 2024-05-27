package br.company.loan.repository;

import br.company.loan.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByIdentifierAndIdentifierType(String identifier, String identifierType);
}
