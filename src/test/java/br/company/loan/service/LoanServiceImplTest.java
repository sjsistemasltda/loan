package br.company.loan.service;

import br.company.loan.controller.exception.LoanMakeException;
import br.company.loan.controller.exception.MaxAmountLoanException;
import br.company.loan.controller.exception.PersonNotFoundException;
import br.company.loan.controller.exception.ProcessPaymentException;
import br.company.loan.entity.Loan;
import br.company.loan.entity.Person;
import br.company.loan.entity.dto.request.LoanCreateRequestDTO;
import br.company.loan.entity.dto.response.LoanResponseDTO;
import br.company.loan.infrastructure.PaymentClient;
import br.company.loan.repository.LoanRepository;
import br.company.loan.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoanServiceImplTest {

    @InjectMocks
    private LoanServiceImpl loanService;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private AwsService awsService;

    @Mock
    private PlatformTransactionManager transactionManager;

    @Mock
    private TransactionStatus transactionStatus;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loanService.queueNameProcessPayment = "test-queue";
        when(transactionManager.getTransaction(any(DefaultTransactionDefinition.class))).thenReturn(transactionStatus);
    }

    @Test
    void testMakeLoanSuccess() {
        LoanCreateRequestDTO loanRequest = new LoanCreateRequestDTO();
        loanRequest.setAmount(BigDecimal.valueOf(1000));
        loanRequest.setInvoiceQuantity(10);

        Person person = new Person();
        person.setId(1L);
        person.setIdentifierType("PF");
        person.setIdentifier("88270742015");
        person.setMaxAmountLoan(BigDecimal.valueOf(10000));
        person.setMinAmountMonthly(BigDecimal.valueOf(100));

        Loan savedLoan = new Loan();
        savedLoan.setId(1L);
        savedLoan.setAmount(loanRequest.getAmount());
        savedLoan.setInvoiceQuantity(loanRequest.getInvoiceQuantity());
        savedLoan.setPerson(person);

        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);
        when(loanRepository.sumAmountByPersonIdAndPaymentStatus(any(Long.class), any(String.class))).thenReturn(BigDecimal.ZERO);
        when(paymentClient.pay(savedLoan.getId())).thenReturn(LoanResponseDTO.convert(savedLoan));

        LoanResponseDTO response = loanService.make(1L, loanRequest);

        assertNotNull(response);
        assertEquals(savedLoan.getAmount(), response.getAmount());
        assertEquals(savedLoan.getInvoiceQuantity(), response.getInvoiceQuantity());
        verify(transactionManager, times(2)).commit(transactionStatus);
    }

    @Test
    void testMakeLoanPersonNotFound() {
        LoanCreateRequestDTO loanRequest = new LoanCreateRequestDTO();
        loanRequest.setAmount(BigDecimal.valueOf(1000));
        loanRequest.setInvoiceQuantity(10);

        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PersonNotFoundException.class, () -> loanService.make(1L, loanRequest));
        verify(transactionManager, times(1)).rollback(transactionStatus);
    }

    @Test
    void testMakeLoanMaxAmountExceeded() {
        LoanCreateRequestDTO loanRequest = new LoanCreateRequestDTO();
        loanRequest.setAmount(BigDecimal.valueOf(10000));
        loanRequest.setInvoiceQuantity(10);

        Person person = new Person();
        person.setId(1L);
        person.setMaxAmountLoan(BigDecimal.valueOf(5000));

        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(loanRepository.sumAmountByPersonIdAndPaymentStatus(1L, "WAITING_PAYMENT")).thenReturn(BigDecimal.valueOf(2000));

        assertThrows(MaxAmountLoanException.class, () -> loanService.make(1L, loanRequest));
        verify(transactionManager, times(1)).rollback(transactionStatus);
    }

    @Test
    void testMakeLoanInternalError() {
        LoanCreateRequestDTO loanRequest = new LoanCreateRequestDTO();
        loanRequest.setAmount(BigDecimal.valueOf(1000));
        loanRequest.setInvoiceQuantity(10);

        Person person = new Person();
        person.setId(1L);
        person.setMaxAmountLoan(BigDecimal.valueOf(5000));

        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(loanRepository.save(any(Loan.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(LoanMakeException.class, () -> loanService.make(1L, loanRequest));
        verify(transactionManager, times(1)).rollback(transactionStatus);
    }

    @Test
    void testMakeLoanPaymentFailure() {
        LoanCreateRequestDTO loanRequest = new LoanCreateRequestDTO();
        loanRequest.setAmount(BigDecimal.valueOf(1000));
        loanRequest.setInvoiceQuantity(10);

        Person person = new Person();
        person.setId(1L);
        person.setIdentifierType("PF");
        person.setIdentifier("88270742015");
        person.setMaxAmountLoan(BigDecimal.valueOf(10000));
        person.setMinAmountMonthly(BigDecimal.valueOf(100));

        Loan savedLoan = new Loan();
        savedLoan.setId(1L);
        savedLoan.setAmount(loanRequest.getAmount());
        savedLoan.setInvoiceQuantity(loanRequest.getInvoiceQuantity());
        savedLoan.setPerson(person);

        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);
        when(paymentClient.pay(savedLoan.getId())).thenThrow(new RuntimeException("Payment error"));
        doThrow(new ProcessPaymentException("Process payment with error")).when(awsService).send(any(), any());
        when(loanRepository.sumAmountByPersonIdAndPaymentStatus(any(Long.class), any(String.class))).thenReturn(BigDecimal.ZERO);

        assertThrows(ProcessPaymentException.class, () -> loanService.make(1L, loanRequest));
        verify(transactionManager, times(2)).commit(transactionStatus); // one for save, one for payment
    }
}