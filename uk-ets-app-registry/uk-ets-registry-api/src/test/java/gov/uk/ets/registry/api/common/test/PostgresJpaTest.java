package gov.uk.ets.registry.api.common.test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/***
 * Annotation for a JPA test that uses the underlying postgres database created
 * by the liquibase statements.
 * Use this annotation in order to take account on the database constraints.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = PostgresSQLContainerInitializer.class)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=validate",
    "spring.liquibase.enabled=true",
    "spring.liquibase.change-log=classpath:liquibase/registry/master_changelog.xml",
    "spring.liquibase.parameters.runtime-user=${spring.datasource.username}",
    "spring.datasource.hikari.maximum-pool-size=5"
})
@DataJpaTest
public @interface PostgresJpaTest {
}
