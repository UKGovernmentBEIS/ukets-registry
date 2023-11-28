package gov.uk.ets.registry.api.common.search;

import com.querydsl.jpa.impl.JPAQuery;
import java.util.Optional;
import java.util.function.LongSupplier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

public abstract class CustomSearch<T> {

    protected JPAQuery<T> query;
    protected Pageable pageable;
    protected LongSupplier totalSupplier;

    /**
     * Constructs a new search for Query DSL using {@link com.querydsl.core.types.Expression}.
     *
     * @param query the query to be executed
     * @param pageable the pagable with the model T we check
     */
    protected CustomSearch(JPAQuery<T> query, Pageable pageable) {
        this.query = Optional.ofNullable(query).orElseThrow(
                () -> new IllegalArgumentException("Query should not be null"));
        this.pageable = Optional.ofNullable(pageable).orElseThrow(
                () -> new IllegalArgumentException("Pageable should not be null"));
    }
    
    /**
     * Constructs a new search for Query DSL using {@link com.querydsl.core.types.Expression}.
     *
     * @param query the query to be executed
     * @param pageable the pagable with the model T we check
     */
    protected CustomSearch(JPAQuery<T> query, Pageable pageable,LongSupplier totalSupplier) {
        this(query,pageable);
        this.totalSupplier = totalSupplier;
    }

    /**
     * Finds the name of the entity property from the passed String. For example if the passed String is "task
     * .request.id",
     * then it returns the "id" String.
     *
     * @param orderProperty The String representation of an entity property.
     * @return The name of entity property
     */
    protected String getEntityProperty(String orderProperty) {
        String[] segments = orderProperty.split("\\.");
        return segments[segments.length - 1];
    }

    /**
     * Applies ordering to the query.
     */
    protected void applyOrdering() {
        pageable.getSort().forEach(order -> this.applyOrder(order, query));
    }

    /**
     * Executes the search query.
     *
     * @return The {@link Page} of results
     */
    public Page<T> getResults() {
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        return PageableExecutionUtils.getPage(query.fetch(), pageable, 
            Optional.ofNullable(totalSupplier).orElse(query::fetchCount));
    }

    /**
     * Returns the query.
     *
     * @return The {@link Page} of results
     */
    public JPAQuery<T> getQuery() {
        return query;
    }

    abstract void applyOrder(Sort.Order order, JPAQuery<T> query);
}
