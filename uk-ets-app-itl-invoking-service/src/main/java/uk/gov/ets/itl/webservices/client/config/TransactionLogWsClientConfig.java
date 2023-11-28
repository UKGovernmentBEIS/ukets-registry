package uk.gov.ets.itl.webservices.client.config;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.rpc.Call;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.handler.HandlerInfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import uk.gov.ets.itl.webservices.client.handler.TranscationLogRequestHandler;
import uk.gov.ets.itl.webservices.client.transactionlog.TransactionLogPort;
import uk.gov.ets.itl.webservices.client.transactionlog.TransactionLogService;
import uk.gov.ets.itl.webservices.client.transactionlog.TransactionLogServiceLocator;

@Configuration
@PropertySource("classpath:application.properties")
public class TransactionLogWsClientConfig {

    @Value("${transactionlog.service.url}")
    private String transactionlogServiceUrl;	
    @Value("${transactionlog.service.username}")
    private String transactionlogServiceUsername;
    @Value("${transactionlog.service.password}")
    private String transactionlogServicePassword;    
    
    /**
     * Declare as a Spring Managed Bean the TransactionLogPort with the required properties.
     * @return
     */
    @Bean
    public TransactionLogPort getTransactionLogPort() {    	
        try {        	
			// Make a service
			TransactionLogService service = new TransactionLogServiceLocator();			
            @SuppressWarnings("unchecked")
			java.util.List<HandlerInfo> list = service.getHandlerRegistry().getHandlerChain(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:1.0:0.0", "TransactionLogPort"));
            Map<String,String> config = new HashMap<>();
            config.put(Call.USERNAME_PROPERTY, transactionlogServiceUsername);
            config.put(Call.PASSWORD_PROPERTY, transactionlogServicePassword);
            list.add(new javax.xml.rpc.handler.HandlerInfo(TranscationLogRequestHandler.class,config,null));
            
			// Now use the service to get a stub which implements the SDI.
			return service.getTransactionLogPort(new URL(transactionlogServiceUrl));
		} catch (MalformedURLException | ServiceException e) {
			throw new RuntimeException("Could not initialize TransactionLogPort",e);
		}        
    }
}
