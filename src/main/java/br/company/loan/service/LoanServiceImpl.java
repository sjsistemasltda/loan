package br.company.loan.service;

import br.company.loan.controller.exception.MaxAmountLoanException;
import br.company.loan.controller.exception.PersonNotFoundException;
import br.company.loan.controller.exception.ProcessPaymentException;
import br.company.loan.entity.Loan;
import br.company.loan.entity.Person;
import br.company.loan.entity.dto.request.LoanCreateRequestDTO;
import br.company.loan.entity.dto.sqs.LoanSenderDTO;
import br.company.loan.infrastructure.PaymentClient;
import br.company.loan.repository.LoanRepository;
import br.company.loan.repository.PersonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final PersonRepository personRepository;
    private final LoanRepository loanRepository;
    private final PaymentClient paymentClient;
    private final AwsService awsService;

    @Value("${payment.service.sqs}")
    private String queueNameProcessPayment;

    private final static String PAYMENT_STATUS_WAITING = "WAITING_PAYMENT";

    @Override
    @Transactional
    public void make(Long personId, LoanCreateRequestDTO loan) {
        Optional<Person> personOptional = personRepository.findById(personId);
        Person person = personOptional.orElseThrow(() -> new PersonNotFoundException("Person not found"));
        validateMaxLoanWithoutPayment(personId, loan.getAmount(), person.getMaxAmountLoan());

        Loan entity = Loan.builder()
                .amount(loan.getAmount())
                .invoiceQuantity(loan.getInvoiceQuantity())
                .person(person)
                .build();

        Loan saved = loanRepository.save(entity);

        try {
            paymentClient.pay(saved.getId());
        } catch (Exception e) {
            sendMessage(
                LoanSenderDTO.builder()
                    .id(saved.getId())
                    .build()
            );
        }
    }

    private void validateMaxLoanWithoutPayment(Long personId, BigDecimal amount, BigDecimal maxAmountPerson) {
        BigDecimal sumLoans = loanRepository.sumAmountByPersonIdAndPaymentStatus(personId, PAYMENT_STATUS_WAITING);
        if(sumLoans.add(amount).compareTo(maxAmountPerson) > 0) {
            throw new MaxAmountLoanException("Reached the maximum loan limit allowed");
        }
    }

    private void sendMessage(LoanSenderDTO loan) {
        try {
            awsService.send(queueNameProcessPayment, loan);
        } catch (Exception e) {
            throw new ProcessPaymentException("Process payment with error");
        }
    }
}
