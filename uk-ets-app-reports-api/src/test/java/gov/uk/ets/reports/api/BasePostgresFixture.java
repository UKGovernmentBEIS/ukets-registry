package gov.uk.ets.reports.api;

import java.util.Collections;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // this on does not work for DataJpaTest
@ContextConfiguration(initializers = BasePostgresFixture.Initializer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // do not create separate context per test
@AutoConfigureTestEntityManager
@ActiveProfiles("test")
public class BasePostgresFixture {
    static final Map<String, String> TMP_FS_MAP = Collections.singletonMap("/var/lib/mysql", "rw");

    @Autowired
    DataSource dataSource;

    @Autowired
    TestEntityManager testEntityManager;

    static final PostgreSQLContainer POSTGRES;

    static {
        POSTGRES = (PostgreSQLContainer) new PostgreSQLContainer("postgres:15.2")
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
            String liquibaseUsername = "spring.liquibase.user=" + POSTGRES.getUsername();
            String liquibasePassword = "spring.liquibase.password=" + POSTGRES.getPassword();

            TestPropertySourceUtils
                .addInlinedPropertiesToEnvironment(environment, postgresUrl, postgresUsername, postgresPassword,
                    liquibaseUsername, liquibasePassword);
        }
    }

    protected void flushAndClear() {
        testEntityManager.flush();
        testEntityManager.clear();
    }
}
