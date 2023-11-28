package gov.uk.ets.registry.api.itl.message.web.model;

import java.util.Date;

import gov.uk.ets.registry.api.common.search.SearchResult;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ITLMessageSearchResult  implements SearchResult {

	private static final long serialVersionUID = 1L;
	
	private Long messageId;
	private Date messageDate;
	private String content;
	private String from;
	private String to;
}
