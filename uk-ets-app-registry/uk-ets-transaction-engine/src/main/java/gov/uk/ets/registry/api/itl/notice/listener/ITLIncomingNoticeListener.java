package gov.uk.ets.registry.api.itl.notice.listener;

import gov.uk.ets.registry.api.itl.notice.ITLNoticeService;
import lombok.extern.log4j.Log4j2;
import uk.gov.ets.lib.commons.kyoto.types.ITLNoticeRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Service for receiving notices from ITL.
 *
 * @author BilisS
 * @since v0.5.0
 */
@Log4j2
@Service
@KafkaListener(topics = "itl.notices.in.topic")
public class ITLIncomingNoticeListener {

    /**
     * Service for ITL notices.
     */
    @Autowired
    private ITLNoticeService itlNoticeService;
    
    /**
     * Handles the incoming notification request.
     * @param request The notification request.
     */
    @KafkaHandler
    public void handleNoticeRequest(ITLNoticeRequest request) {
        log.info("Received a notification request from ITL: {}", request);
        itlNoticeService.processIncomingNotice(request);
    }

}
