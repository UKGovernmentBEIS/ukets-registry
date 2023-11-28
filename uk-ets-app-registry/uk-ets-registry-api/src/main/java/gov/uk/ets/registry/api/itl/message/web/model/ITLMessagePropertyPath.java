package gov.uk.ets.registry.api.itl.message.web.model;

public class ITLMessagePropertyPath {
	  private ITLMessagePropertyPath() {}

	  /**
	   * The full identifier property path
	   */
	  public static final String MESSAGE_IDENTIFIER = "acceptMessageLog.Id";
	  
	  /**
	   * The from property path
	   */
	  public static final String MESSAGE_FROM = "acceptMessageLog.source";
	  
	  /**
	   * The to property path
	   */
	  public static final String MESSAGE_TO = "acceptMessageLog.destination";
	  
	  /**
	   * The receive date of the message
	   */
	  public static final String MESSAGE_DATE = "acceptMessageLog.messageDatetime";
	  /**
	   * The content of the message
	   */
	  public static final String MESSAGE_CONTENT = "acceptMessageLog.content";
}
