package uk.gov.ets.itl.webservices.client.handler;

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;

/**
 * @author P35036
 * @since v.0.5.0
 */
public class TranscationLogRequestHandler implements Handler {

	/** Handler Configuration Map.*/
	private Map<String,String> config;
	
	@Override
	public void destroy() {
		//Auto-generated method stub

	}

	@Override
	public QName[] getHeaders() {
		//Auto-generated method stub
		return null;
	}

	@Override
	public boolean handleFault(MessageContext msgContext) {
		//Auto-generated method stub
		return true;
	}

	@Override
	public boolean handleRequest(MessageContext msgContext) {
		addAuthorizationProperties(msgContext);
		return true;
	}

	@Override
	public boolean handleResponse(MessageContext msgContext) {
		//Auto-generated method stub
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(HandlerInfo handlerInfo) {
		this.config = handlerInfo.getHandlerConfig();
	}

	/**
	 * Adds Basic Authorization credentials as required by ITL.
	 * @param msgContext
	 */
	private void addAuthorizationProperties(MessageContext msgContext) {
		msgContext.setProperty(Call.USERNAME_PROPERTY, config.get(Call.USERNAME_PROPERTY));
		msgContext.setProperty(Call.PASSWORD_PROPERTY, config.get(Call.PASSWORD_PROPERTY));
	}
}
