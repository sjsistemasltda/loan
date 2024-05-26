package br.company.loan.controller;

import br.company.loan.Constants;
import br.company.loan.entity.dto.request.LoanCreateRequestDTO;
import br.company.loan.entity.dto.response.LoanResponseDTO;
import br.company.loan.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping(value = Constants.CONTROLLER.PERSON.LOAN.PATH)
    public ResponseEntity<?> make(@PathVariable("id") Long personId, @RequestBody @Valid LoanCreateRequestDTO loan) {
        LoanResponseDTO entity = loanService.make(personId, loan);
        return ResponseEntity.created(URI.create("loans"))
                .body(entity);
    }
}
