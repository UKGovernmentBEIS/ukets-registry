package gov.uk.ets.registry.api.payment.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import gov.uk.ets.registry.api.payment.domain.Payment;
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
    
    @BeforeEach
    public void setUp() throws Exception {
        Payment payment = new Payment();
        payment.setReferenceNumber(1274L);
        
        entityManager.persist(payment);
    }
    
    @Test
    public void findByUrlSuffix() {
        Payment payment = new Payment();
        entityManager.persist(payment);
    	String uuid = payment.getUrlSuffix();
        Optional<Payment> result = paymentRepository.findByUrlSuffix(uuid);
        assertTrue(result.isPresent());
    }
    
    @Test
    public void findByReferenceNumber() {
        Optional<Payment> result = paymentRepository.findByReferenceNumber(1274L);
        assertTrue(result.isPresent());
    }
}
