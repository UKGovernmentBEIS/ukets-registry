package uk.gov.ets.transaction.log.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;


@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create"})
public abstract class BaseJpaTest extends BaseTest {

    @Autowired
    protected TestEntityManager entityManager;

    /**
     * Calls flush to force synchronization (all delayed SQL queries will be done) and clear
     * to allow fetching entities again from the database.
     */
    protected void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
