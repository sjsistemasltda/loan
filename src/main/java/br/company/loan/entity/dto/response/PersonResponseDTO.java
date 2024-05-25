package br.company.loan.entity.dto.response;

import br.company.loan.entity.Person;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class PersonResponseDTO {

    public static PersonResponseDTO convert(Person person) {
        return PersonResponseDTO.builder()
                .id(person.getId())
                .name(person.getName())
                .identifier(person.getIdentifier())
                .identifierType(person.getIdentifierType())
                .birthDate(person.getBirthDate())
                .minAmountMonthly(person.getMinAmountMonthly())
                .maxAmountLoan(person.getMaxAmountLoan())
                .build();
    }

    public Long id;

    public String name;

    public String identifier;

    public String identifierType;

    public LocalDate birthDate;

    public BigDecimal minAmountMonthly;

    public BigDecimal maxAmountLoan;
}
