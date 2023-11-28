package gov.uk.ets.registry.api.itl.message.web.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(of = {"messageId"})
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ITLMessageSummary {

	private Long messageId;
	private String content;
	private Date messageDate;
    private String source;
    private String destination;    
}
