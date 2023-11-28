package uk.gov.ets.kp.webservices.shared.enums;

/**
 * Enumerates possible values for ResultIdentifier.
 * @author P35036
 */
public enum ResultIdentifier {

	SOAP_MESSAGE_REJECTED(0), 
	SOAP_MESSAGE_ACCEPTED(1);

	private final int code;

	ResultIdentifier(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
