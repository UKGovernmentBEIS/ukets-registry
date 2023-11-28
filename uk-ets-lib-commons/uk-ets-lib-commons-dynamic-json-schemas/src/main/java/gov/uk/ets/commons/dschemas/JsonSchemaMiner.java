package gov.uk.ets.commons.dschemas;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * We want to generate a schema for a specific set of objects and keep it in memory for validating http requests
 * down the road.
 *
 * This class Scans the spring context for methods following a specific methodFilteringCondition.
 * When these methods are found then parameters following the parameterFilteringCondition are returned.
 * The type of each such parameter is then transformed to a Json schema.
 * All such schemas are stored in the schemaRegistry.
 */
@Component
@RequiredArgsConstructor
public final class JsonSchemaMiner implements ApplicationListener<ContextRefreshedEvent> {

    private final JsonSchemaRegistry schemaRegistry;
    private final JsonSchemaGenerator schemaGenerator;

    /**
     * @see ApplicationListener#onApplicationEvent(ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();

        applicationContext.getBean(RequestMappingHandlerMapping.class)
            .getHandlerMethods()
            .forEach(
                (requestMappingInfo, handlerMethod) -> {
                    Optional<Annotation> optionalAnnotation = Stream.of(handlerMethod.getMethod().getAnnotations())
                        .filter(methodFilteringCondition())
                        .findFirst();

                    if (optionalAnnotation.isPresent()) {
                        Optional<MethodParameter> optionalMethodParameter =
                            Arrays.stream(handlerMethod.getMethodParameters())
                                .filter(parameterFilteringCondition())
                                .findFirst();

                        if (optionalMethodParameter.isPresent()) {
                            Class<?> parameterType = optionalMethodParameter.get().getParameterType();
                            schemaRegistry.add(parameterType, schemaGenerator.generateJsonSchema(parameterType));
                        }
                    }
                }
            );
    }

    private Predicate<MethodParameter> parameterFilteringCondition() {
        return methodParameter -> methodParameter.hasParameterAnnotation(ValidJson.class);
    }

    private Predicate<Annotation> methodFilteringCondition() {
        return annotation -> annotation instanceof PostMapping
                || annotation instanceof PutMapping
                || annotation instanceof DeleteMapping
                || annotation instanceof PatchMapping;
    }

}
