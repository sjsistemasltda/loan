package br.company.loan.service;

import br.company.loan.controller.exception.PersonAlreadyExistsException;
import br.company.loan.controller.exception.PersonNotFoundException;
import br.company.loan.entity.Person;
import br.company.loan.entity.dto.request.PersonCreateRequestDTO;
import br.company.loan.entity.dto.request.PersonUpdateRequestDTO;
import br.company.loan.entity.dto.response.PersonResponseDTO;
import br.company.loan.enums.LoanAmount;
import br.company.loan.enums.PersonType;
import br.company.loan.repository.PersonRepository;
import br.company.loan.service.factory.IdentifierValidType;
import br.company.loan.service.factory.IdentifierValidTypeFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final IdentifierValidTypeFactory identifierValidTypeFactory;

    @Override
    @Transactional
    public PersonResponseDTO create(PersonCreateRequestDTO person) {
        try {
            LoanAmount loanAmount = LoanAmount.getByName(person.getIdentifierType());
            IdentifierValidType identifierValidType = identifierValidTypeFactory.get(
                    PersonType.getByName(person.getIdentifierType())
            );
            identifierValidType.validate(person.getIdentifier());

            Person entity = Person.builder()
                    .name(person.getName())
                    .identifier(person.getIdentifier())
                    .identifierType(person.getIdentifierType())
                    .birthDate(person.getBirthDate())
                    .minAmountMonthly(loanAmount.getMinAmountMonthly())
                    .maxAmountLoan(loanAmount.getMaxAmountLoan())
                    .build();

            Person entitySaved = personRepository.save(entity);

            return PersonResponseDTO.convert(entitySaved);
        } catch (DataIntegrityViolationException ex) {
            throw new PersonAlreadyExistsException("Person already exists");
        }

    }

    @Override
    public PersonResponseDTO get(Long id) {
        Person person = this.findById(id);
        return PersonResponseDTO.convert(person);
    }

    @Override
    @Transactional
    public PersonResponseDTO update(Long id, PersonUpdateRequestDTO person) {
        Person entity = this.findById(id);
        entity.setBirthDate(person.getBirthDate());
        entity.setName(person.getName());
        Person entitySaved = personRepository.save(entity);
        return PersonResponseDTO.convert(entitySaved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Person person = this.findById(id);
        personRepository.delete(person);
    }

    private Person findById(Long id) {
        Optional<Person> personOptional = personRepository.findById(id);
        return personOptional.orElseThrow(() -> new PersonNotFoundException("Person not found"));
    }
}
