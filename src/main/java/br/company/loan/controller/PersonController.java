package br.company.loan.controller;

import br.company.loan.Constants;
import br.company.loan.entity.dto.request.PersonCreateRequestDTO;
import br.company.loan.entity.dto.request.PersonUpdateRequestDTO;
import br.company.loan.entity.dto.response.PersonResponseDTO;
import br.company.loan.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @PostMapping(value = Constants.CONTROLLER.PERSON.PATH)
    public ResponseEntity<?> create(@Valid @RequestBody PersonCreateRequestDTO person) {
        PersonResponseDTO response = personService.create(person);
        return ResponseEntity.created(URI.create("person"))
                .body(response);
    }

    @PutMapping(value = Constants.CONTROLLER.PERSON.UPDATE.PATH)
    public ResponseEntity<?> update(
            @PathVariable(value = "id") Long id,
            @Valid @RequestBody PersonUpdateRequestDTO person
    ) {
        PersonResponseDTO response = personService.update(id, person);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = Constants.CONTROLLER.PERSON.GET.PATH)
    public ResponseEntity<?> get(@PathVariable(value = "id") Long id) {
        PersonResponseDTO response = personService.get(id);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = Constants.CONTROLLER.PERSON.DELETE.PATH)
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
