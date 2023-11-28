package gov.uk.ets.registry.api.itl.message.web.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ITLMessageSearchCriteria implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long messageId;
	
	/**
	 * The message date (from)
	 */
	@DateTimeFormat(iso = ISO.DATE)
	private Date messageDateFrom;

	/**
	 * The message date (until)
	 */
	@DateTimeFormat(iso = ISO.DATE)
	private Date messageDateTo;

}
