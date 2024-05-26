package br.company.loan.service;

import br.company.loan.controller.exception.LoanMakeException;
import br.company.loan.controller.exception.MaxAmountLoanException;
import br.company.loan.controller.exception.MaxInvoiceQuantityException;
import br.company.loan.controller.exception.MinAmountLoanException;
import br.company.loan.controller.exception.PersonNotFoundException;
import br.company.loan.controller.exception.ProcessPaymentException;
import br.company.loan.entity.Loan;
import br.company.loan.entity.Person;
import br.company.loan.entity.dto.request.LoanCreateRequestDTO;
import br.company.loan.entity.dto.response.LoanResponseDTO;
import br.company.loan.entity.dto.sqs.LoanSenderDTO;
import br.company.loan.infrastructure.PaymentClient;
import br.company.loan.repository.LoanRepository;
import br.company.loan.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanServiceImpl implements LoanService {

    private final PersonRepository personRepository;
    private final LoanRepository loanRepository;
    private final PaymentClient paymentClient;
    private final AwsService awsService;
    private final PlatformTransactionManager transactionManager;

    @Value("${payment.service.sqs}")
    private String queueNameProcessPayment;

    private final static String PAYMENT_STATUS_WAITING = "WAITING_PAYMENT";

    @Override
    public LoanResponseDTO make(Long personId, LoanCreateRequestDTO loan) {
        TransactionStatus saveStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
        Loan saved;

        try {
            Optional<Person> personOptional = personRepository.findById(personId);
            Person person = personOptional.orElseThrow(() -> new PersonNotFoundException("Person not found"));
            validateMaxLoanWithoutPayment(personId, loan.getAmount(), person.getMaxAmountLoan());

            Loan entity = Loan.builder()
                    .amount(loan.getAmount())
                    .invoiceQuantity(loan.getInvoiceQuantity())
                    .person(person)
                    .build();

            saved = loanRepository.save(entity);
            transactionManager.commit(saveStatus);
        } catch (MaxInvoiceQuantityException
                 | MinAmountLoanException
                 | MaxAmountLoanException
                 | PersonNotFoundException  e
        ) {
            transactionManager.rollback(saveStatus);
            throw e;
        } catch (Exception e) {
            transactionManager.rollback(saveStatus);
            throw new LoanMakeException("Internal error to persist loan");
        }
        makePayment(saved);

        return LoanResponseDTO.convert(saved);
    }

    private void makePayment(Loan loan) {
        TransactionStatus paymentStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            LoanResponseDTO loanPaid = paymentClient.pay(loan.getId());
            loan.setPaymentStatus(loanPaid.getPaymentStatus());
            transactionManager.commit(paymentStatus);
        } catch (Exception e) {
            try {
                sendMessage(
                    LoanSenderDTO.builder()
                        .id(loan.getId())
                        .build()
                );
            } catch (ProcessPaymentException ep) {
                loanRepository.delete(loan);
                transactionManager.commit(paymentStatus);
                throw new ProcessPaymentException("Failed to process payment");
            }
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
            System.out.println(e.getMessage());
            throw new ProcessPaymentException("Process payment with error");
        }
    }
}
