package gov.uk.ets.registry.api.itl.message.domain;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a transaction.
 */
@Entity
@Getter
@Setter
@Table(name = "accept_message_log")
public class AcceptMessageLog {


    @Id
    @SequenceGenerator(name = "accept_message_log_id_generator", sequenceName = "accept_message_log_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accept_message_log_id_generator")
    private Long id;

    @Column(name = "source")
    private String source;
    
    @Column(name = "destination")
    private String destination;    
    
    @Column(name = "message_content")
    private String content; 

	@Column(name = "message_datetime")
	private Date messageDatetime;
	
	@Column(name = "status_datetime")
	private Date statusDatetime;

    @Column(name = "status_description")
    private String statusDescription; 
    
}
