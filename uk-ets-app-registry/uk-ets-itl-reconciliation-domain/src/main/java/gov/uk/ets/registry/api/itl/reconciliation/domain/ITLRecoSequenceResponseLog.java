package gov.uk.ets.registry.api.itl.reconciliation.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "itl_reco_sequence_response_log")
public class ITLRecoSequenceResponseLog {

    @Id
    @SequenceGenerator(name = "itl_reco_sequence_response_log_id_generator", sequenceName = "itl_reco_sequence_response_log_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itl_reco_sequence_response_log_id_generator")
    private Long id;
    
    @Column(name = "recon_id")
    private String reconId;
    
    @Column(name = "response_code")
    private Integer responseCode;
    
    private Date datetime;
    
    @PrePersist
    void datetime() {
        this.datetime = new Date();
    }
}
