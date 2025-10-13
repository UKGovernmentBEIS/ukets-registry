package gov.uk.ets.registry.api.payment.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.common.search.OptionalBooleanBuilder;
import gov.uk.ets.registry.api.common.search.Search;
import gov.uk.ets.registry.api.payment.domain.PaymentFilter;
import gov.uk.ets.registry.api.payment.domain.PaymentHistoryProjection;
import gov.uk.ets.registry.api.payment.domain.QPaymentHistory;
import gov.uk.ets.registry.api.payment.domain.QPaymentHistoryProjection;
import gov.uk.ets.registry.api.payment.shared.PaymentHistoryPropertyPath;
import gov.uk.ets.registry.api.task.domain.QTask;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * Implementation of custom repository of search payments.
 */
public class PaymentHistorySearchRepositoryImpl implements PaymentHistorySearchRepository {

    private static final QPaymentHistory paymentHistory = QPaymentHistory.paymentHistory;
    private static final QTask task = new QTask("taskOfPayment");
    
    @PersistenceContext
    EntityManager entityManager;
    
    
    private Map<String, EntityPathBase<?>> sortingMap = Stream.of(new Object[][] {
        {PaymentHistoryPropertyPath.REFERENCE_NUMBER, paymentHistory},
        {PaymentHistoryPropertyPath.PAYMENT_ID, paymentHistory},
        {PaymentHistoryPropertyPath.PAYMENT_TYPE, paymentHistory},
        {PaymentHistoryPropertyPath.PAYMENT_METHOD, paymentHistory},
        {PaymentHistoryPropertyPath.PAYMENT_STATUS, paymentHistory},
        {PaymentHistoryPropertyPath.PAYMENT_AMOUNT, paymentHistory},
        {PaymentHistoryPropertyPath.LAST_UPDATED, paymentHistory}
    }).collect(Collectors.toMap(
        data -> (String) data[0],
        data -> (EntityPathBase<?>) data[1]));
    
    /**
     * Searches the payment history with the supplied criteria.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Page<PaymentHistoryProjection> search(PaymentFilter filter, Pageable pageable) {
        return new Search.Builder<PaymentHistoryProjection>()
            .pageable(pageable)
            .sortingMap(sortingMap)
            .query(getQuery(filter))
            .build()
            .getResults();
    }
    
    /**
     * Constructs a JPA query using the provided filter.
     */
    @Override
    public JPAQuery<PaymentHistoryProjection> getQuery(PaymentFilter filter) {

        JPAQuery<PaymentHistoryProjection> query = new JPAQuery<PaymentHistoryProjection>(entityManager)
            .select(createPaymentHistoryProjection())
            .from(paymentHistory);

        BooleanExpression condition = new OptionalBooleanBuilder(paymentHistory.isNotNull())
                .build();

        if (Boolean.FALSE.equals(filter.getAdminSearch())) {
            BooleanExpression userPaymentTasks = paymentHistory.referenceNumber.in(
                    JPAExpressions.select(task.requestId)
                    .from(task)
                    .where(task.user.urid.eq(filter.getUrid())));
            condition = condition.and(userPaymentTasks);
        }

        if (Optional.ofNullable(filter.getReferenceNumber()).isPresent()) {
            BooleanExpression referenceNumberExpression = paymentHistory.referenceNumber.eq(filter.getReferenceNumber());
            condition = condition.and(referenceNumberExpression);
            
        }
        
        return query.where(condition);    

    }
    
    
    private QPaymentHistoryProjection createPaymentHistoryProjection() {
        return new QPaymentHistoryProjection(
           paymentHistory.referenceNumber,
           paymentHistory.paymentId,
           paymentHistory.type,
           paymentHistory.method,
           paymentHistory.status,
           paymentHistory.amount,
           paymentHistory.updated
        );
    }
}
