package gov.uk.ets.registry.api.payment.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create"
})
public class PaymentRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PaymentRepository paymentRepository;
}
