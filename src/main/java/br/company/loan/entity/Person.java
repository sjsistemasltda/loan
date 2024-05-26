package br.company.loan.entity;

import br.company.loan.Constants;
import br.company.loan.enums.LoanAmount;
import br.company.loan.enums.PersonType;
import br.company.loan.service.factory.IdentifierValidType;
import br.company.loan.service.factory.IdentifierValidTypeFactory;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = Constants.RDS.TABLE.PERSON.NAME)
@Table(schema = Constants.RDS.SCHEMA)
@Builder(builderClassName = "PersonBuilder")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_seq")
    @SequenceGenerator(name = "person_seq", sequenceName = Constants.RDS.TABLE.PERSON.SEQ, allocationSize = 1)
    private Long id;

    @Column(name = "name", nullable = false)
    @Size(min = 1, max = 50)
    public String name;

    @Column(name = "identifier", nullable = false)
    @Size(min = 1, max = 14)
    public String identifier;

    @Column(name = "identifier_type", nullable = false)
    @Size(min = 2, max = 2)
    public String identifierType;

    @Column(name = "birth_date", nullable = false)
    public LocalDate birthDate;

    @Column(name = "min_amount_monthly", nullable = false, precision = 18, scale = 2)
    public BigDecimal minAmountMonthly;

    @Column(name = "max_amount_loan", nullable = false, precision = 18, scale = 2)
    public BigDecimal maxAmountLoan;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDate createdAt;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Loan> loans = new LinkedHashSet<>();

    public Person(
            String name,
            String identifier,
            String identifierType,
            LocalDate birthDate,
            BigDecimal minAmountMonthly,
            BigDecimal maxAmountLoan
    ) {
        this.name = name;
        this.identifier = identifier;
        this.identifierType = identifierType;
        this.birthDate = birthDate;
        this.minAmountMonthly = minAmountMonthly;
        this.maxAmountLoan = maxAmountLoan;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Person person = (Person) o;
        return getId() != null && Objects.equals(getId(), person.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public void validate() {
        IdentifierValidTypeFactory identifierValidTypeFactory = new IdentifierValidTypeFactory();
        IdentifierValidType identifierValidType = identifierValidTypeFactory.get(
                PersonType.getByName(this.getIdentifierType())
        );
        identifierValidType.validate(this.getIdentifier());
    }

    public static class PersonBuilder {
        public Person build() {
            LoanAmount loanAmount = LoanAmount.getByName(identifierType);
            Person person = new Person(
                    name,
                    identifier,
                    identifierType,
                    birthDate,
                    loanAmount.getMinAmountMonthly(),
                    loanAmount.getMaxAmountLoan()
            );
            person.validate();
            return person;
        }
    }
}
