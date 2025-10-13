package gov.uk.ets.registry.api.payment.web.model;

import gov.uk.ets.registry.api.common.search.PageParameters;
import gov.uk.ets.registry.api.common.search.PageableMapper;
import gov.uk.ets.registry.api.payment.shared.PaymentHistoryPropertyPath;
import java.util.function.Function;
import org.springframework.data.domain.Sort;

/**
 * Mapper which responsibility is to map the received {@link PageParameters} params 
 * to a {@link org.springframework.data.domain.Pageable} object.
 */
public class PaymentHistorySearchPageableMapper extends PageableMapper {

    public PaymentHistorySearchPageableMapper() {
        super(SortFieldParam.values(), SortFieldParam.PAYMENT_ID);
    }

    /**
     * The Sort field param enum. It maps a sort filed request parameter to a {@link Sort} object.
     */
    public enum SortFieldParam implements SortParameter {

      REFERENCE_NUMBER("referenceNumber", direction ->
          Sort.by(direction != null ? direction : Sort.Direction.ASC, PaymentHistoryPropertyPath.REFERENCE_NUMBER)),
      PAYMENT_ID("paymentId", direction ->
        Sort.by(direction != null ? direction : Sort.Direction.ASC, PaymentHistoryPropertyPath.PAYMENT_ID)),
      PAYMENT_TYPE("type", direction ->
      Sort.by(direction != null ? direction : Sort.Direction.ASC, PaymentHistoryPropertyPath.PAYMENT_TYPE)),
      PAYMENT_METHOD("method", direction ->
      Sort.by(direction != null ? direction : Sort.Direction.ASC, PaymentHistoryPropertyPath.PAYMENT_METHOD)),
      PAYMENT_STATUS("status", direction ->
      Sort.by(direction != null ? direction : Sort.Direction.ASC, PaymentHistoryPropertyPath.PAYMENT_STATUS)),
      PAYMENT_AMOUNT("amount", direction ->
      Sort.by(direction != null ? direction : Sort.Direction.ASC, PaymentHistoryPropertyPath.PAYMENT_AMOUNT)),
      LAST_UPDATED("updated", direction ->
      Sort.by(direction != null ? direction : Sort.Direction.ASC, PaymentHistoryPropertyPath.LAST_UPDATED));
      
        private String sortField;
        private Function<Sort.Direction, Sort> getSortFunc;

        SortFieldParam(String sortField, Function<Sort.Direction, Sort> getSortFunc) {
            this.sortField = sortField;
            this.getSortFunc = getSortFunc;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getName() {
            return sortField;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Function<Sort.Direction, Sort> getSortProvider() {
            return getSortFunc;
        }
    }
}