package uk.gov.ets.transaction.log.domain;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"identifier"})
public class Account {

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "account_id_generator", sequenceName = "account_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_id_generator")
    private Long id;

    /**
     * The account name.
     */
    @Column(name = "account_name")
    private String accountName;

    /**
     * The check digits.
     */
    @Column(name = "check_digits")
    private Integer checkDigits;

    /**
     * The commitment period code.
     */
    @Column(name = "commitment_period_code")
    private Integer commitmentPeriodCode;

    /**
     * The identifier.
     */
    private Long identifier;

    /**
     * The full account identifier
     */
    @NaturalId
    @Column(name = "full_identifier")
    private String fullIdentifier;
  
    /**
     * The opening date.
     */
    @Column(name = "opening_date")
    private Date openingDate;
}
