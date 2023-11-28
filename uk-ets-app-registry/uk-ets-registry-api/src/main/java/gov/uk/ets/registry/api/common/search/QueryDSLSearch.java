package gov.uk.ets.registry.api.common.search;

import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;

public class QueryDSLSearch<T> extends CustomSearch<T> {
    private Map<String, Expression<?>> sortingMap;

    private QueryDSLSearch(JPAQuery<T> query, Pageable pageable, Map<String, Expression<?>> sortingMap) {
        super(query, pageable);
        this.sortingMap = Optional.ofNullable(sortingMap).orElseThrow(
                () -> new IllegalArgumentException("sortingMap should not be null"));
        applyOrdering();
    }

    /**
     * Applies order to the search query.
     *
     * @param order The {@link Sort.Order} of pagination
     * @param query The query of search
     */
     protected void applyOrder(Sort.Order order, JPAQuery<T> query) {
        String property = order.getProperty();
        Expression<?> expression = sortingMap.get(property);
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
        if (expression == null) {
            return;
        } else if (expression instanceof ConstructorExpression &&
            expression.getClass().getSuperclass().isAssignableFrom(ConstructorExpression.class)) {
                String propertySuffix = getEntityProperty(property);
                Order orderDirection = order.isAscending() ? Order.ASC : Order.DESC;
                ConstructorExpression constructorExpression = ConstructorExpression.class.cast(expression);
                for (Object expressionArg : constructorExpression.getArgs()) {
                    if (expressionArg instanceof Expression &&
                        Expression.class.cast(expressionArg).toString().endsWith(propertySuffix)) {
                            if (expressionArg instanceof Operation) {
                                List<Expression> expressionListOfOperation = Operation.class.cast(expressionArg).getArgs();
                                if (expressionListOfOperation != null && expressionListOfOperation.size() >= 1 &&
                                        expressionListOfOperation.get(expressionListOfOperation.size() - 1).toString().endsWith(propertySuffix)) {
                                            orderSpecifiers.add(new OrderSpecifier(orderDirection, Expressions.template(String.class,
                                                    expressionListOfOperation.get(expressionListOfOperation.size() - 1).toString())));
                                }
                            } else {
                                orderSpecifiers.add(new OrderSpecifier(orderDirection, Expression.class.cast(expressionArg)));
                            }
                    }
                }
        } else {
            Order orderDirection = order.isAscending() ? Order.ASC : Order.DESC;
            orderSpecifiers.add(new OrderSpecifier(orderDirection, expression));
        }
        OrderSpecifier[] orderSpecifiersArray = new OrderSpecifier[orderSpecifiers.size()];
        orderSpecifiersArray = orderSpecifiers.toArray(orderSpecifiersArray);
        query.orderBy(orderSpecifiersArray);
     }

    /**
     * Builder for building {@link QueryDSLSearch} instances.
     *
     * @param <T> The Search Result type.
     */
    public static class Builder<T> {
        private JPAQuery<T> query;
        private Pageable pageable;
        private Map<String, Expression<?>> sortingMap;

        /**
         * Adding the query property.
         *
         * @param query The {@link JPAQuery} query of search
         * @return This {@link Builder} instance.
         */
        public Builder query(JPAQuery<T> query) {
            this.query = query;
            return this;
        }

        /**
         * Adding the pageable property.
         *
         * @param pageable The pagination information of query
         * @return This {@link Builder} instance.
         */
        public Builder pageable(Pageable pageable) {
            this.pageable = pageable;
            return this;
        }

        /**
         * Adding the sortingMap property.
         *
         * @param sortingMap The property path to {@link Expression} object map.
         * @return This {@link Builder} instance.
         */
        public Builder sortingMap(Map<String, Expression<?>> sortingMap) {
            this.sortingMap = sortingMap;
            return this;
        }

        /**
         * Building the search object.
         *
         * @return The {@link QueryDSLSearch} instance
         */
        public QueryDSLSearch<T> build() {
            return new QueryDSLSearch(query, pageable, sortingMap);
        }
    }
}
