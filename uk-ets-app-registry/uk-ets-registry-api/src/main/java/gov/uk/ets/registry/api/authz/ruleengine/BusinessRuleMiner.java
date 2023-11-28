package gov.uk.ets.registry.api.authz.ruleengine;

import gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule;
import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Component
@RequiredArgsConstructor
public class BusinessRuleMiner {

    private final BusinessRuleStore businessRuleStore;

    /**
     * Extract business rules from all beans with the {@link Protected} annotation.
     * @param event the triggering event
     */
    public void mine(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();

        applicationContext.getBean(RequestMappingHandlerMapping.class)
            .getHandlerMethods()
            .forEach(
                (requestMappingInfo, handlerMethod) -> {
                    Optional<Annotation> optionalAnnotation = Stream.of(handlerMethod.getMethod().getAnnotations())
                        .filter(annotation -> annotation instanceof Protected)
                        .findFirst();
                    Class<? extends AbstractBusinessRule>[] classArray =
                        optionalAnnotation.map(annotation -> ((Protected) annotation).value())
                            .orElseGet(() -> new Class[] {});
                    getUrlPattern(requestMappingInfo).forEach(
                        pattern -> businessRuleStore.put(pattern, classArray)
                    );
                }
            );
    }

    private Set<String> getUrlPattern(RequestMappingInfo requestMappingInfo) {
        return requestMappingInfo.getPatternsCondition().getPatterns();
    }
}
