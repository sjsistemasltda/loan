package br.company.loan.entity;

import br.company.loan.Constants;
import br.company.loan.controller.exception.MaxAmountLoanException;
import br.company.loan.controller.exception.MaxInvoiceQuantityException;
import br.company.loan.controller.exception.MinAmountLoanException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

@Entity(name = Constants.RDS.TABLE.LOAN.NAME)
@Table(schema = Constants.RDS.SCHEMA)
@Builder(builderClassName = "LoanBuilder")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_seq")
    @SequenceGenerator(name = "loan_seq", sequenceName = Constants.RDS.TABLE.LOAN.SEQ, allocationSize = 1)
    private Long id;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "invoice_quantity", nullable = false)
    private Integer invoiceQuantity;

    @Column(name = "payment_status", nullable = false)
    @Size(min = 4, max = 50)
    private String paymentStatus;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDate createdAt;

    public Loan(BigDecimal amount, Integer invoiceQuantity, Person person, String status) {
        this.amount = amount;
        this.invoiceQuantity = invoiceQuantity;
        this.person = person;
        this.paymentStatus = status;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Loan loan = (Loan) o;
        return getId() != null && Objects.equals(getId(), loan.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public void validate() {
        if(invoiceQuantity > 24) {
            throw new MaxInvoiceQuantityException("More than 24 invoices quantities");
        }

        if (amount.compareTo(person.getMaxAmountLoan()) > 0) {
            throw new MaxAmountLoanException("Amount exceeds the maximum allowed loan amount for the person");
        }

        BigDecimal mediaAmount = amount.divide(BigDecimal.valueOf(invoiceQuantity), RoundingMode.HALF_EVEN);

        if(person.getMinAmountMonthly().compareTo(mediaAmount) > 0){
            throw new MinAmountLoanException("The minimum installment value divided by the quantity is: " + person.getMinAmountMonthly());
        }
    }

    public static class LoanBuilder {
        public Loan build() {
            Loan loan = new Loan(amount, invoiceQuantity, person, "WAITING_PAYMENT");
            loan.validate();
            return loan;
        }
    }
}
