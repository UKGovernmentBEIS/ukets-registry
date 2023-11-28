package gov.uk.ets.commons.dschemas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * This class intercepts incoming web requests before they reach the controller. This specific feature of Spring was
 * used (HandlerMethodArgumentResolver) because it is the only place we have access to:
 * <ul>
 *     <li>the NativeWebRequest for validating the Json payload</li>
 *     <li>information on the parameter type and information on the annotations that are defined</li>
 * </ul>
 */
@RequiredArgsConstructor
public final class JsonSchemaValidatingArgumentResolver implements HandlerMethodArgumentResolver, InitializingBean {

    private final JsonSchemaRegistry jsonSchemaRegistry;
    private final JsonValidator jsonValidator;
    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Checks if the resolver supports a specific parameter.
     * @param methodParameter the parameter to support
     * @return true ior false
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(ValidJson.class);
    }

    /**
     * For incoming web requests, this resolver will read the JSON payload and then validate it with a schema found in
     * the schema registry.
     *
     * @param methodParameter the method
     * @param modelAndViewContainer the mvc object
     * @param nativeWebRequest the actual wen request as it came from the client
     * @param webDataBinderFactory internal factory
     * @return the resolved object after it has passed schema validation
     * @throws Exception in case of Validation errors
     */
    @Override
    @Nullable
    public Object resolveArgument(
        MethodParameter methodParameter, @Nullable ModelAndViewContainer modelAndViewContainer,
        NativeWebRequest nativeWebRequest, @Nullable WebDataBinderFactory webDataBinderFactory)
            throws Exception {
        Class<?> parameterType = methodParameter.getParameterType();
        JsonNode json = objectMapper.readTree(getJsonPayload(nativeWebRequest));
        try {
            jsonValidator.validate(json, jsonSchemaRegistry.get(parameterType));
        } catch (Exception e) {
            throw new JsonValidationFailedException();
        }
        return objectMapper.treeToValue(json, methodParameter.getParameterType());
    }

    private String getJsonPayload(NativeWebRequest nativeWebRequest) throws IOException {
        HttpServletRequest httpServletRequest = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        return StreamUtils.copyToString(httpServletRequest.getInputStream(), StandardCharsets.UTF_8);
    }

    /**
     * Used to add this custom resolver before other default resolvers. See this ticket for more information on this
     * workaround: https://github.com/spring-projects/spring-framework/issues/23043
     */
    @Override
    public void afterPropertiesSet() {
        List<HandlerMethodArgumentResolver> argumentResolvers = requestMappingHandlerAdapter.getArgumentResolvers();
        if (argumentResolvers == null) {
            argumentResolvers = new LinkedList<>();
        }
        List<HandlerMethodArgumentResolver> newArgumentResolvers = new LinkedList<>();
        newArgumentResolvers.add(this);
        newArgumentResolvers.addAll(argumentResolvers);
        requestMappingHandlerAdapter.setArgumentResolvers(Collections.unmodifiableList(newArgumentResolvers));
    }
}
