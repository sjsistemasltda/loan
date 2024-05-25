package br.company.loan.entity.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PersonUpdateRequestDTO {

    @NotNull(message = "name can not be null")
    @Size(min = 1, max = 50, message = "name length needs to be between 3 and 50")
    public String name;

    @NotNull(message = "birthDate can not be null")
    public LocalDate birthDate;
}
