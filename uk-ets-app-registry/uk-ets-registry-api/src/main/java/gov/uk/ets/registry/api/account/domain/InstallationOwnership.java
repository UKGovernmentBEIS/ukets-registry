package gov.uk.ets.registry.api.account.domain;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table(name = "installation_ownership")
@Getter
@Setter
@ToString
@SequenceGenerator(name = "installation_ownership_id_generator", sequenceName = "installation_ownership_seq", allocationSize = 1)
@Entity
public class InstallationOwnership {

    /**
     * The id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "installation_ownership_id_generator")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "installation_id")
    private Installation installation;

    @Column(name = "ownership_date")
    private Date ownershipDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private InstallationOwnershipStatus status;

    @Column(name = "permit_identifier")
    private String permitIdentifier;

}
