package br.company.loan.repository;

import br.company.loan.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query(value = "SELECT COALESCE(SUM(l.amount), 0) FROM bank.loan l WHERE l.person_id = :personId AND l.payment_status = :paymentStatus", nativeQuery = true)
    BigDecimal sumAmountByPersonIdAndPaymentStatus(
            @Param("personId") Long personId,
            @Param("paymentStatus") String paymentStatus
    );
}
