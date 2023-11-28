package gov.uk.ets.commons.logging;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Log4j2
@Component
public class StartupShutdownEventListener {

    @Autowired
    private BuildProperties buildProperties;
    @Autowired
    private Config config;

    @EventListener
    void onStartup(ApplicationReadyEvent event) {
        MDCWrapper mdcWrapper = new MDCWrapper();
        fillAppAttrMDC(mdcWrapper);
        log.info("Application started.");
        mdcWrapper.clear();
    }

    @EventListener
    void onShutdown(ContextClosedEvent event) {
        MDCWrapper mdcWrapper = new MDCWrapper();
        fillAppAttrMDC(mdcWrapper);
        log.info("Application closed.");
        mdcWrapper.clear();
    }

    private void fillAppAttrMDC(MDCWrapper mdc) {
        mdc.put(MDCWrapper.Attr.APP_NAME, buildProperties != null ? buildProperties.getName() : "");
        mdc.put(MDCWrapper.Attr.APP_VERSION, buildProperties != null ? buildProperties.getVersion() : "");
        mdc.put(MDCWrapper.Attr.APP_COMMIT_ID, config != null && config.getCommitId() != null ? config.getCommitId()[0] : "");
        mdc.put(MDCWrapper.Attr.APP_COMMIT_DATE, config != null && config.getCommitTime() != null ? config.getCommitTime()[0] : "");
    }
}