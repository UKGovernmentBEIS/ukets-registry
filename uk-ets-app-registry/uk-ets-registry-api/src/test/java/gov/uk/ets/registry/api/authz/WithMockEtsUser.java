package gov.uk.ets.registry.api.authz;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockEtsUserSecurityContextFactory.class)
public @interface WithMockEtsUser {

    /** The user roles.*/
    String[] roles() default { "ets_user" };
    
    /** The user scopes.*/
    String[] scopes() default { };

    /** The user state.*/
    String state() default  "ENROLLED";
    
    /** The user state.*/
    String urid() default  "UK802061511788";
}
