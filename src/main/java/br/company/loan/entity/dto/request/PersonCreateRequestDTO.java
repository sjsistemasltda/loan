package br.company.loan.entity.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PersonCreateRequestDTO {

    @NotNull(message = "name can not be null")
    @Size(min = 3, max = 50, message = "name length needs to be between 3 and 50")
    public String name;

    @NotNull(message = "identifier can not be null")
    @Size(min = 1, max = 14, message = "identifier length needs to be between 1 and 14")
    public String identifier;

    @NotNull(message = "identifierType can not be null")
    @Size(min = 2, max = 2, message = "identifierType length needs to be 2")
    public String identifierType;

    @NotNull(message = "birthDate can not be null")
    public LocalDate birthDate;
}
