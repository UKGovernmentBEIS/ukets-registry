package gov.uk.ets.registry.api.authz.ruleengine;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import jakarta.annotation.Resource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * Aspect that makes sure that the @RuleInput annotation is processed and its values are stored.
 */
@Component
public class RuleInputLoader {

    @Resource(name = "requestScopedRuleInputStore")
    private RuleInputStore ruleInputStore;

    /**
     * The actual method that implements tha aspect.
     *
     * @param point the join point
     */
    public void processInput(JoinPoint point) {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Object[] args = point.getArgs();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int argIndex = 0; argIndex < args.length; argIndex++) {
            for (Annotation paramAnnotation : parameterAnnotations[argIndex]) {
                if (!(paramAnnotation instanceof RuleInput)) {
                    continue;
                }
                RuleInput dataAnnotation = (RuleInput) paramAnnotation;
                ruleInputStore.put(dataAnnotation.value(), args[argIndex]);
            }
        }
    }
}
