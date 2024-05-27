package br.company.loan.service;

import br.company.loan.controller.exception.InvalidIdentifierException;
import br.company.loan.controller.exception.PersonAlreadyExistsException;
import br.company.loan.controller.exception.PersonNotFoundException;
import br.company.loan.entity.Person;
import br.company.loan.entity.dto.request.PersonCreateRequestDTO;
import br.company.loan.entity.dto.request.PersonUpdateRequestDTO;
import br.company.loan.entity.dto.response.PersonResponseDTO;
import br.company.loan.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PersonServiceImplTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonServiceImpl personServiceImpl;

    private PersonCreateRequestDTO personCreateRequestDTO;
    private Person personEntity;
    private PersonResponseDTO personResponseDTO;

    @BeforeEach
    void setUp() {
        personCreateRequestDTO = new PersonCreateRequestDTO();
        personCreateRequestDTO.setName("John Doe");
        personCreateRequestDTO.setIdentifier("88270742015");
        personCreateRequestDTO.setIdentifierType("PF");
        personCreateRequestDTO.setBirthDate(LocalDate.of(1990, 1, 1));

        personEntity = Person.builder()
                .name("John Doe")
                .identifier("88270742015")
                .identifierType("PF")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        personResponseDTO = PersonResponseDTO.convert(personEntity);
    }

    @Test
    void testGetPersonSuccess() {
        when(personRepository.findById(any())).thenReturn(Optional.of(personEntity));

        PersonResponseDTO result = personServiceImpl.get(1L);

        assertNotNull(result);
        assertEquals(personResponseDTO.getName(), result.getName());
        assertEquals(personResponseDTO.getIdentifier(), result.getIdentifier());
        verify(personRepository, times(1)).findById(any());
    }

    @Test
    void testGetPersonNotFound() {
        when(personRepository.findById(any())).thenReturn(Optional.empty());

        Exception exception = assertThrows(PersonNotFoundException.class, () -> {
            personServiceImpl.get(1L);
        });

        assertEquals("Person not found", exception.getMessage());
        verify(personRepository, times(1)).findById(any());
    }

    @Test
    void testCreatePersonSuccess() {
        when(personRepository.save(any(Person.class))).thenReturn(personEntity);

        PersonResponseDTO result = personServiceImpl.create(personCreateRequestDTO);

        assertNotNull(result);
        assertEquals(personResponseDTO.getName(), result.getName());
        assertEquals(personResponseDTO.getIdentifier(), result.getIdentifier());
        verify(personRepository, times(1)).save(any(Person.class));
    }

    @Test
    void testUpdatePersonSuccess() {
        personEntity.setName("Novo nome");
        when(personRepository.findById(any())).thenReturn(Optional.of(personEntity));
        when(personRepository.save(any(Person.class))).thenReturn(personEntity);

        PersonUpdateRequestDTO personUpdateRequestDTO = new PersonUpdateRequestDTO();
        personUpdateRequestDTO.setName("Novo nome");
        PersonResponseDTO result = personServiceImpl.update(1L, personUpdateRequestDTO);

        assertNotNull(result);
        assertEquals(personUpdateRequestDTO.getName(), result.getName());
        verify(personRepository, times(1)).save(any(Person.class));
    }

    @Test
    void testCreatePersonAlreadyExistsException() {
        when(personRepository.save(any(Person.class))).thenThrow(DataIntegrityViolationException.class);

        Exception exception = assertThrows(PersonAlreadyExistsException.class, () -> {
            personServiceImpl.create(personCreateRequestDTO);
        });

        assertEquals("Person already exists", exception.getMessage());
        verify(personRepository, times(1)).save(any(Person.class));
    }

    @Test
    void testPersonWrongCPF() {
        personCreateRequestDTO.setIdentifier("123456789");
        Exception exception = assertThrows(InvalidIdentifierException.class, () -> {
            personServiceImpl.create(personCreateRequestDTO);
        });

        assertEquals("Invalid CPF", exception.getMessage());
    }

    @Test
    void testPersonWrongCNPJ() {
        personCreateRequestDTO.setIdentifierType("PJ");
        personCreateRequestDTO.setIdentifier("37555558000156");
        Exception exception = assertThrows(InvalidIdentifierException.class, () -> {
            personServiceImpl.create(personCreateRequestDTO);
        });

        assertEquals("Invalid CNPJ", exception.getMessage());
    }

    @Test
    void testPersonWrongEU() {
        personCreateRequestDTO.setIdentifierType("EU");
        personCreateRequestDTO.setIdentifier("12345673");
        Exception exception = assertThrows(InvalidIdentifierException.class, () -> {
            personServiceImpl.create(personCreateRequestDTO);
        });

        assertEquals("Invalid University Student", exception.getMessage());
    }

    @Test
    void testPersonWrongAP() {
        personCreateRequestDTO.setIdentifierType("AP");
        personCreateRequestDTO.setIdentifier("33324211199");
        Exception exception = assertThrows(InvalidIdentifierException.class, () -> {
            personServiceImpl.create(personCreateRequestDTO);
        });

        assertEquals("Invalid Retired", exception.getMessage());
    }
}
