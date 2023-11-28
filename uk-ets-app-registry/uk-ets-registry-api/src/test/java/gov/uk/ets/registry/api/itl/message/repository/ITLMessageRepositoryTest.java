package gov.uk.ets.registry.api.itl.message.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import gov.uk.ets.registry.api.itl.message.domain.AcceptMessageLog;
import gov.uk.ets.registry.api.itl.message.web.model.ITLMessagePropertyPath;
import gov.uk.ets.registry.api.itl.message.web.model.ITLMessageSearchCriteria;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create"})
public class ITLMessageRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ITLMessageRepository messageRepository;
    

    @Test
    public void findByMessageId() {

        Stream.of(1L, 2L, 3L, 4L, 5L).forEach(id -> {
            AcceptMessageLog message = new AcceptMessageLog();
            message.setSource("UK ETS");
            message.setDestination("ITL");
            message.setMessageDatetime(new Date());
            message.setContent("A content.");
            message.setStatusDatetime(new Date());
            message.setStatusDescription("Send.");
            
            entityManager.persist(message);
        });
    	
        Optional<AcceptMessageLog> messageOptional = messageRepository.findById(5L);
        assertTrue(messageOptional.isPresent());
        
        Optional<AcceptMessageLog> messageEmptyOptional = messageRepository.findById(11L);
        assertFalse(messageEmptyOptional.isPresent());
    }

    @Test
    public void searchByMessageId() {
    	
        AcceptMessageLog message = new AcceptMessageLog();
        message.setSource("UK ETS");
        message.setDestination("ITL");
        message.setMessageDatetime(new Date());
        message.setContent("A content.");
        message.setStatusDatetime(new Date());
        message.setStatusDescription("Send.");
        
        entityManager.persist(message);
    	
    	
    	ITLMessageSearchCriteria criteria = new ITLMessageSearchCriteria();
        criteria.setMessageId(message.getId());
        Page<AcceptMessageLog> results = messageRepository.search(criteria, PageRequest.of(0, 10));
        assertEquals(1, results.getNumberOfElements());
    }
    
    @Test
    public void searchByMessageDateFrom() {
    	LocalDateTime messageDateTime = LocalDateTime.of(2020,Month.NOVEMBER,9,12,21,29);
    	Date messageDate = Date.from(messageDateTime.toInstant(ZoneOffset.UTC));
        AcceptMessageLog message = new AcceptMessageLog();
        message.setSource("UK ETS");
        message.setDestination("ITL");
        message.setMessageDatetime(messageDate);
        message.setContent("A content.");
        message.setStatusDatetime(new Date());
        message.setStatusDescription("Send.");
        
        entityManager.persist(message);
    	
    	
    	ITLMessageSearchCriteria criteria = new ITLMessageSearchCriteria();
        criteria.setMessageDateFrom(messageDate);
        Page<AcceptMessageLog> results = messageRepository.search(criteria, PageRequest.of(0, 10));
        assertEquals(1, results.getNumberOfElements());
       
    }
    
    @Test
    public void sortByMessageDate() {
    	LocalDateTime messageDateTime_9_12 = LocalDateTime.of(2020,Month.NOVEMBER,9,12,21,29);
    	Date messageDate_9_12 = Date.from(messageDateTime_9_12.toInstant(ZoneOffset.UTC));
        AcceptMessageLog message_9_12 = new AcceptMessageLog();
        message_9_12.setSource("UK ETS");
        message_9_12.setDestination("ITL");
        message_9_12.setMessageDatetime(messageDate_9_12);
        message_9_12.setContent("A content.");
        message_9_12.setStatusDatetime(new Date());
        message_9_12.setStatusDescription("Send.");
        
        entityManager.persist(message_9_12);  	
    	
    	LocalDateTime messageDateTime_13_12 = LocalDateTime.of(2020,Month.NOVEMBER,13,12,21,29);
    	Date messageDate_13_12 = Date.from(messageDateTime_13_12.toInstant(ZoneOffset.UTC));
        AcceptMessageLog message_13_12 = new AcceptMessageLog();
        message_13_12.setSource("UK ETS");
        message_13_12.setDestination("ITL");
        message_13_12.setMessageDatetime(messageDate_13_12);
        message_13_12.setContent("A content.");
        message_13_12.setStatusDatetime(new Date());
        message_13_12.setStatusDescription("Send.");
        
        entityManager.persist(message_13_12);  
        
    	ITLMessageSearchCriteria criteria = new ITLMessageSearchCriteria();
        Page<AcceptMessageLog> results = messageRepository.search(criteria, PageRequest.of(0, 10 , Sort.by(Direction.DESC, ITLMessagePropertyPath.MESSAGE_DATE)));
        assertEquals(2, results.getNumberOfElements());
        assertEquals(message_13_12.getMessageDatetime(), results.getContent().get(0).getMessageDatetime());
        
    }    
    
    @Test
    public void searchByMessageDateTo() {
    	LocalDateTime messageDateTime = LocalDateTime.of(2020,Month.NOVEMBER,9,12,21,29);
    	Date messageDate = Date.from(messageDateTime.toInstant(ZoneOffset.UTC));
        AcceptMessageLog message = new AcceptMessageLog();
        message.setSource("UK ETS");
        message.setDestination("ITL");
        message.setMessageDatetime(messageDate);
        message.setContent("A content.");
        message.setStatusDatetime(new Date());
        message.setStatusDescription("Send.");
        
        entityManager.persist(message);
    	
    	
    	ITLMessageSearchCriteria criteria = new ITLMessageSearchCriteria();
        criteria.setMessageDateTo(messageDate);
        Page<AcceptMessageLog> results = messageRepository.search(criteria, PageRequest.of(0, 10));
        assertEquals(1, results.getNumberOfElements());
        
    }
    
    @Test
    public void searchByMessageDateBetween() {
    	LocalDateTime messageDateTime = LocalDateTime.of(2020,Month.NOVEMBER,9,12,21,29);
    	Date messageDate = Date.from(messageDateTime.toInstant(ZoneOffset.UTC));
        AcceptMessageLog message = new AcceptMessageLog();
        message.setSource("UK ETS");
        message.setDestination("ITL");
        message.setMessageDatetime(messageDate);
        message.setContent("A content.");
        message.setStatusDatetime(new Date());
        message.setStatusDescription("Send.");
        
        entityManager.persist(message);
    	
    	
    	ITLMessageSearchCriteria criteria = new ITLMessageSearchCriteria();
        //3 days before 
        LocalDateTime threeDaysBeforeDateTime = messageDateTime.minusDays(3);
        criteria.setMessageDateFrom(Date.from(threeDaysBeforeDateTime.toInstant(ZoneOffset.UTC)));
        
        //3 days after 
        LocalDateTime threeDaysAfterDateTime = messageDateTime.plusDays(3);
        criteria.setMessageDateTo( Date.from(threeDaysAfterDateTime.toInstant(ZoneOffset.UTC)));

        Page<AcceptMessageLog> results = messageRepository.search(criteria, PageRequest.of(0, 10));
        assertEquals(1, results.getNumberOfElements());
    }
}
