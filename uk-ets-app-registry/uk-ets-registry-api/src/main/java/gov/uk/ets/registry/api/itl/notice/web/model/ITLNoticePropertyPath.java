package gov.uk.ets.registry.api.itl.notice.web.model;

/**
 * The property path used for {@link ITLNoticeResult itlNoticeResult}
 */
public class ITLNoticePropertyPath {
	  private ITLNoticePropertyPath() {}

	  /**
	   * The full identifier property path
	   */
	  public static final String NOTICE_IDENTIFIER = "itlNoticeResult.notificationIdentifier";
	  
	  /**
	   * The date when the notice was last updated
	   */
	  public static final String NOTICE_DATE_RECEIVED_ON = "itlNoticeResult.receivedOn";

	  /**
	   * The date when the notice was last created
	   */
	  public static final String NOTICE_DATE_UPDATED_ON = "itlNoticeResult.lastUpdateOn";

	  /**
	   * the notice type
	   */
	  public static final String NOTICE_TYPE = "itlNoticeResult.type";

	  /**
	   * the notice status
	   */
	  public static final String NOTICE_STATUS = "itlNoticeResult.status";



}
