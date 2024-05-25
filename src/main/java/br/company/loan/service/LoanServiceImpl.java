package br.company.loan.service;

import br.company.loan.controller.exception.PersonNotFoundException;
import br.company.loan.entity.Loan;
import br.company.loan.entity.Person;
import br.company.loan.entity.dto.request.LoanCreateRequestDTO;
import br.company.loan.repository.LoanRepository;
import br.company.loan.repository.PersonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final PersonRepository personRepository;
    private final LoanRepository loanRepository;

    @Override
    @Transactional
    public void make(Long personId, LoanCreateRequestDTO loan) {
        Optional<Person> personOptional = personRepository.findById(personId);
        Person person = personOptional.orElseThrow(() -> new PersonNotFoundException("Person not found"));
        Loan entity = Loan.builder()
                .amount(loan.getAmount())
                .invoiceQuantity(loan.getInvoiceQuantity())
                .person(person)
                .build();

        Loan saved = loanRepository.save(entity);
    }
}
