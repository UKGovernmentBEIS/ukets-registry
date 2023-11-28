package gov.uk.ets.registry.api.account.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
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
