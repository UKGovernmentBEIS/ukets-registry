package gov.uk.ets.commons.logging;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static gov.uk.ets.commons.logging.MDCWrapper.Attr.RESOURCE_ID;
import static gov.uk.ets.commons.logging.MDCWrapper.Attr.RESOURCE_TYPE;

@Aspect
@Component
@Log4j2
public class MDCParamProcessor {

    @Before("execution(* *(.., @MDCParam (*), ..))")
    public void processAnnotatedMethodParameter(JoinPoint point) {
        try {
            Method method = ((MethodSignature) point.getSignature()).getMethod();
            Object[] args = point.getArgs();
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();

            for (int argIndex = 0; argIndex < args.length; argIndex++) {
                for (Annotation parameterAnnotation : parameterAnnotations[argIndex]) {
                    if (parameterAnnotation instanceof MDCParam) {
                        MDCParam mdcParam = (MDCParam) parameterAnnotation;
                        RequestParamType mdcParamType = mdcParam.value();
                        Object arg = args[argIndex];
                        //if @MDCParam exists on a method parameter that is not DTO, add parameter in MDC and exit
                        if (!RequestParamType.DTO.equals(mdcParamType)) {
                            MDCWrapper.getOne().put(RESOURCE_TYPE, mdcParamType.name()).put(RESOURCE_ID, arg.toString());
                            break;
                        } else {
                            traverseDTO(arg);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Exception in MDCParam annotation processing: " + e);
        }
    }

    private void traverseDTO(Object arg) throws IllegalAccessException {
        Field[] declaredFields = arg.getClass().getDeclaredFields();
        for (Field f : declaredFields) {
            MDCParam mdcParam = f.getAnnotation(MDCParam.class);
            Object fieldValue = null;
            //if MDCParam exists for this field, get the field value
            if (mdcParam != null) {
                f.setAccessible(true);
                fieldValue = f.get(arg);
            }
            //if MDCParam exists for this field and the field value is not null
            if (mdcParam != null && !ObjectUtils.isEmpty(fieldValue)) {
                //if field value is not DTO, add field in MDC and exit
                if (!RequestParamType.DTO.equals(mdcParam.value())) {
                    MDCWrapper.getOne().put(RESOURCE_TYPE, mdcParam.value().name())
                            .put(RESOURCE_ID, fieldValue.toString());
                    return;
                }
                //if field value is DTO, traverse it
                else {
                    traverseDTO(fieldValue);
                }
            }
        }
    }
}
