package gov.uk.ets.registry.api.itl.message.web.model;

import gov.uk.ets.registry.api.itl.message.domain.AcceptMessageLog;

public class ITLMessageSearchResultMapper {

	  /**
	   * Maps a {@link AcceptMessageLog} to a {@link ITLMessageSearchResult}
	   * @param projection The {@link AcceptMessageLog}
	   * @return {@link ITLMessageSearchResult}
	   */
	  public ITLMessageSearchResult map(AcceptMessageLog acceptMessageLog) {
	    return ITLMessageSearchResult.builder()
	        .messageId(acceptMessageLog.getId())
	        .messageDate(acceptMessageLog.getMessageDatetime())
	        .from(acceptMessageLog.getSource())
	        .to(acceptMessageLog.getDestination())
	        .content(acceptMessageLog.getContent())
	        .build();
	  }
	  
}
