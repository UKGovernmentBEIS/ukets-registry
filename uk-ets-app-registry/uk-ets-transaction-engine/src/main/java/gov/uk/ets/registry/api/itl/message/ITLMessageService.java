package gov.uk.ets.registry.api.itl.message;

import gov.uk.ets.registry.api.itl.message.domain.AcceptMessageLog;
import gov.uk.ets.registry.api.itl.message.repository.AcceptMessageLogRepository;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import gov.uk.ets.registry.api.transaction.messaging.ITLOutgoingMessageService;
import java.util.Calendar;
import java.util.Date;
import lombok.AllArgsConstructor;
import uk.gov.ets.lib.commons.kyoto.types.MessageRequest;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

@Service
@AllArgsConstructor
public class ITLMessageService {

    /**
     * Service for sending messages to ITL.
     */
    private final ITLOutgoingMessageService itlOutgoingMessageService;

    private final AcceptMessageLogRepository acceptMessageLogRepository;

    /**
     * Processes an incoming transaction.
     *
     * @param request The proposal request.
     */
    @Async
    @Transactional
    public void processIncomingMessage(MessageRequest request) {
        AcceptMessageLog acceptMessageLog = toAcceptMessageLog(request);
        acceptMessageLog.setStatusDatetime(new Date());
        acceptMessageLog.setStatusDescription("Received from Kafka Queue.");
        acceptMessageLogRepository.save(acceptMessageLog);
    }


    @Transactional(propagation = Propagation.MANDATORY)
    public AcceptMessageLog sendMessage(String content) {
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setMajorVersion(Constants.ITL_MAJOR_VERSION);
        messageRequest.setMinorVersion(Constants.ITL_MINOR_VERSION);
        messageRequest.setFrom(Constants.KYOTO_REGISTRY_CODE);
        messageRequest.setTo(Constants.ITL_TO);
        messageRequest.setMessageDateTime(Calendar.getInstance());
        messageRequest.setMessageContent(HtmlUtils.htmlEscape(content));
        itlOutgoingMessageService.sendMessageRequest(messageRequest);

        AcceptMessageLog acceptMessageLog = toAcceptMessageLog(messageRequest);
        acceptMessageLog.setContent(HtmlUtils.htmlUnescape(content));
        acceptMessageLog.setStatusDatetime(new Date());
        acceptMessageLog.setStatusDescription("Send to Kafka Queue.");
        return acceptMessageLogRepository.save(acceptMessageLog);
    }

    private AcceptMessageLog toAcceptMessageLog(MessageRequest messageRequest) {
        AcceptMessageLog acceptMessageLog = new AcceptMessageLog();
        acceptMessageLog.setContent(messageRequest.getMessageContent());
        acceptMessageLog.setDestination(messageRequest.getTo());
        acceptMessageLog.setSource(messageRequest.getFrom());
        acceptMessageLog.setMessageDatetime(messageRequest.getMessageDateTime().getTime());

        return acceptMessageLog;
    }
}
