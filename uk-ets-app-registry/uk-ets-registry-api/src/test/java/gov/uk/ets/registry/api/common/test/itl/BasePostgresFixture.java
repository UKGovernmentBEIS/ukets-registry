package gov.uk.ets.registry.api.common.test.itl;

import java.util.Collections;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = BasePostgresFixture.Initializer.class)
public class BasePostgresFixture {
    static final Map<String, String> TMP_FS_MAP = Collections.singletonMap("/var/lib/mysql", "rw");

    @Autowired
    DataSource dataSource;

    static PostgreSQLContainer POSTGRES;

    static {
        POSTGRES = (PostgreSQLContainer) new PostgreSQLContainer("postgres:12.1")
            .withTmpFs(TMP_FS_MAP)
            .withReuse(true)
        ;

        POSTGRES.start();
    }

    public static class Initializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {

            ConfigurableEnvironment environment = configurableApplicationContext.getEnvironment();

            String postgresUrl = "spring.datasource.url=" + POSTGRES.getJdbcUrl();
            String postgresUsername = "spring.datasource.username=" + POSTGRES.getUsername();
            String postgresPassword = "spring.datasource.password=" + POSTGRES.getPassword();

            TestPropertySourceUtils
                .addInlinedPropertiesToEnvironment(environment, postgresUrl, postgresUsername, postgresPassword);
        }
    }
}
