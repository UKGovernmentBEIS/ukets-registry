package gov.uk.ets.registry.api.account.domain;

import gov.uk.ets.registry.api.account.web.model.accountcontact.MetsAccountContactType;
import gov.uk.ets.registry.api.account.web.model.accountcontact.OperatorType;
import gov.uk.ets.registry.api.common.model.services.converter.StringTrimConverter;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "mets_account_contact")
public class MetsAccountContact implements Serializable {

    private static final long serialVersionUID = -4512389765432109876L;

    @Id
    @SequenceGenerator(name = "mets_account_contact_id_generator", sequenceName = "mets_account_contact_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mets_account_contact_id_generator")
    private Long id;


    @Convert(converter = StringTrimConverter.class)
    @Column(name = "contact_name", nullable = false)
    private String name;

    @Convert(converter = StringTrimConverter.class)
    @Column(name = "email_address", nullable = false)
    private String emailAddress;

    @Convert(converter = StringTrimConverter.class)
    @Column(name = "phone_number_1")
    private String phoneNumber1;

    @Column(name = "phone_number_1_country")
    private String countryCode1;

    @Convert(converter = StringTrimConverter.class)
    @Column(name = "phone_number_2")
    private String phoneNumber2;

    @Column(name = "phone_number_2_country")
    private String countryCode2;

    @Builder.Default
    @ElementCollection(targetClass = MetsAccountContactType.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "mets_account_contact_type",
            joinColumns = @JoinColumn(name = "mets_account_contact_id", nullable = false)
    )
    @Column(name = "contact_type")
    private Set<MetsAccountContactType> contactTypes = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "operator_type")
    private OperatorType operatorType;

    @Column(name = "invited_date")
    private LocalDateTime invitedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

}
