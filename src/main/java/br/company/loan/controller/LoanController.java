package br.company.loan.controller;

import br.company.loan.Constants;
import br.company.loan.entity.dto.request.LoanCreateRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoanController {

    @PostMapping(value = Constants.CONTROLLER.PERSON.LOAN.PATH)
    public ResponseEntity<?> make(@PathVariable("id") Long personId, @RequestBody @Valid LoanCreateRequestDTO loan) {

        return ResponseEntity.ok().build();
    }
}
