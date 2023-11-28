package gov.uk.ets.registry.api.tal.web.model.search;

import com.querydsl.core.annotations.QueryProjection;
import gov.uk.ets.registry.api.common.search.SearchResult;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TALProjection implements SearchResult {

    private Long id;
    private String accountFullIdentifier;
    private Boolean underSameAccountHolder;
    private String description;
    private String name;
    private TrustedAccountStatus status;
    private ZonedDateTime activationDate;

    /**
     * @param id
     * @param accountFullIdentifier
     * @param underSameAccountHolder
     * @param description
     * @param name
     * @param status
     * @param activationDate
     *
     * */

    @QueryProjection
    public TALProjection(Long id,
                         String accountFullIdentifier,
                         Boolean underSameAccountHolder,
                         String description,
                         String name,
                         TrustedAccountStatus status,
                         LocalDateTime activationDate){
        this.id = id;
        this.accountFullIdentifier = accountFullIdentifier;
        this.underSameAccountHolder = underSameAccountHolder;
        this.description = description;
        this.name = name;
        this.status = status;
        Optional.ofNullable(activationDate).ifPresent(t -> {
            this.activationDate = t.atZone(ZoneId.systemDefault()); 
        });
    }

    @QueryProjection
    public TALProjection(Long id,
                         String accountFullIdentifier,
                         Boolean underSameAccountHolder,
                         String name
                        ){
        this.id = id;
        this.accountFullIdentifier = accountFullIdentifier;
        this.underSameAccountHolder = underSameAccountHolder;
        this.name = name;
    }
}
