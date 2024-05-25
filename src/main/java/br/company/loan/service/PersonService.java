package br.company.loan.service;

import br.company.loan.entity.dto.request.PersonCreateRequestDTO;
import br.company.loan.entity.dto.request.PersonUpdateRequestDTO;
import br.company.loan.entity.dto.response.PersonResponseDTO;

public interface PersonService {
    PersonResponseDTO create(PersonCreateRequestDTO person);

    PersonResponseDTO get(Long id);

    PersonResponseDTO update(Long id, PersonUpdateRequestDTO person);

    void delete(Long id);
}
