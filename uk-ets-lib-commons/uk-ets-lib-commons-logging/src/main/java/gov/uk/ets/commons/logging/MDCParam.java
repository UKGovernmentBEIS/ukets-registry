package gov.uk.ets.commons.logging;

import java.lang.annotation.*;

/**
 * Use this annotation to specify that the annotated parameter will be added in MDC.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface MDCParam {

    RequestParamType value();

}
