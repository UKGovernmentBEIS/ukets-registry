package gov.uk.ets.registry.api.common.search;

import static com.querydsl.core.types.Order.ASC;
import static com.querydsl.core.types.Order.DESC;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BeanPath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.LongSupplier;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyPath;

/**
 * A generic search class.
 *
 * @param <T> The Search Result type.
 */
public class Search<T> extends CustomSearch<T> {

    private Map<String, BeanPath<?>> sortingMap;
    private Map<String, Class<?>> aliasesMap;  
    
    private Search(JPAQuery<T> query, Pageable pageable, Map<String, BeanPath<?>> sortingMap) {
        this(query, pageable, sortingMap, null);
    }
    
    private Search(JPAQuery<T> query, Pageable pageable, 
        Map<String, BeanPath<?>> sortingMap, Map<String, Class<?>> aliasesMap) {
        this(query, pageable, sortingMap, aliasesMap,null);
    }

    private Search(JPAQuery<T> query, Pageable pageable, 
        Map<String, BeanPath<?>> sortingMap, Map<String, Class<?>> aliasesMap,LongSupplier countQuery) {
        super(query, pageable,countQuery);
        this.sortingMap = Optional.ofNullable(sortingMap).orElseThrow(
            () -> new IllegalArgumentException("sortingMap should not be null"));
        this.aliasesMap = aliasesMap;
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
        Order orderDirection = order.isAscending() ? ASC : DESC;
        
        BeanPath<?> pathBase = sortingMap.get(property);

        if (Objects.nonNull(pathBase)) {
            String entityProperty = getEntityProperty(property);
            Expression<?> sortPropertyExpression = buildOrderPropertyPathFrom(order, pathBase, entityProperty);
            query.orderBy(new OrderSpecifier(orderDirection, sortPropertyExpression));
        } else if (Objects.nonNull(aliasesMap) && Objects.nonNull(aliasesMap.get(property))) {
            Class<?> aliasClass = aliasesMap.get(property);
            query.orderBy(new OrderSpecifier(orderDirection, Expressions.path(aliasClass, property)));         
        }

    }

    /**
     * Creates an {@link Expression} for the given property, for the
     * {@link EntityPathBase} pathBase that this property targets to and for the given
     * {@link Sort.Order}.
     *
     * @param order          must not be {@literal null}.
     * @param entityProperty The String representation of the entity property
     * @param pathBase       the {@link EntityPathBase} entity path of the entity that has this property.
     * @return the {@link Expression} for the given {@link Sort.Order} property
     */
    private Expression<?> buildOrderPropertyPathFrom(Sort.Order order, BeanPath<?> pathBase,
                                                     String entityProperty) {
        PropertyPath path = PropertyPath.from(entityProperty, pathBase.getType());
        Expression<?> sortPropertyExpression = pathBase;

        while (path != null) {
            if (!path.hasNext() && order.isIgnoreCase()) {
                // if order is ignore-case we have to treat the last path segment as a String.
                sortPropertyExpression =
                    Expressions.stringPath((Path<?>) sortPropertyExpression, path.getSegment()).lower();
            } else {
                sortPropertyExpression =
                    Expressions.path(path.getType(), (Path<?>) sortPropertyExpression, path.getSegment());
            }

            path = path.next();
        }

        return sortPropertyExpression;
    }

    /**
     * Builder for building {@link Search} instances.
     *
     * @param <T> The Search Result type.
     */
    public static class Builder<T> {
        private JPAQuery<T> query;
        private Pageable pageable;
        private Map<String, BeanPath<?>> sortingMap;
        private Map<String, Class<?>> aliasesMap;
        private LongSupplier totalSupplier;
        
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
         * @param sortingMap The property path to {@link EntityPathBase} object map.
         * @return This {@link Builder} instance.
         */
        public Builder sortingMap(Map<String, BeanPath<?>> sortingMap) {
            this.sortingMap = sortingMap;
            return this;
        }
        
        /**
         * Adding the aliasesMap property.
         *
         * @param aliasesMap A map with key the alias name and value the class the alias represents.
         * @return This {@link Builder} instance.
         */
        public Builder aliasesMap(Map<String, Class<?>> aliasesMap) {
            this.aliasesMap = aliasesMap;
            return this;
        }

        /**
         * Adding the countQuery property.
         *
         * @param countQuery A custom count query.
         * @return This {@link Builder} instance.
         */
        public Builder countQuery(LongSupplier totalSupplier) {
            this.totalSupplier = totalSupplier;
            return this;
        }        
        
        /**
         * Building the search object.
         *
         * @return The {@link Search} instance
         */
        public Search<T> build() {
            return new Search(query, pageable, sortingMap,aliasesMap,totalSupplier);
        }
    }
}
