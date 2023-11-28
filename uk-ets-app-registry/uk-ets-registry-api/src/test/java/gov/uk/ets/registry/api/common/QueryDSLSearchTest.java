package gov.uk.ets.registry.api.common;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.account.domain.QAccount;
import gov.uk.ets.registry.api.common.search.Search;
import gov.uk.ets.registry.api.task.shared.TaskPropertyPath;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.Map;

public class QueryDSLSearchTest {
    private static class TestProjection {
        private String testField;

        public String getTestField() {
            return testField;
        }
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private JPAQuery<QueryDSLSearchTest.TestProjection> query;
    private Expression testPathBase;
    private Map<String, Expression<?>> sortingMap;
    private String testSortProperty;

    @Before
    public void setUp() throws Exception {
        testSortProperty = TaskPropertyPath.ACCOUNT_IDENTIFIER;
        testPathBase = QAccount.account;
        query = Mockito.mock(JPAQuery.class);
        sortingMap = new HashMap<>();
        sortingMap.put(testSortProperty, testPathBase);
    }

    @Test
    public void building_a_Search_without_query_should_throw_IllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Query should not be null");
        new Search.Builder().pageable(PageRequest.of(0, 10)).sortingMap(sortingMap).build();

        thrown.expectMessage("Pageable should not be null");
        new Search.Builder().query(query).sortingMap(sortingMap).build();
    }

    @Test
    public void building_a_Search_without_pageable_should_throw_IllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Pageable should not be null");
        new Search.Builder().query(query).sortingMap(sortingMap).build();
    }

    @Test
    public void building_a_Search_without_sortingMap_should_throw_IllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("sortingMap should not be null");
        new Search.Builder().query(query).pageable(PageRequest.of(0, 10)).build();
    }

    @Test
    public void calling_getResults_method_of_a_Search_which_has_pageable_without_sorting_should_apply_pagination_to_the_query_and_fetch_the_query_results() {
        JPAQuery<QueryDSLSearchTest.TestProjection> query = Mockito.mock(JPAQuery.class);
        int page = 0;
        int pageSize = 10;
        Search<QueryDSLSearchTest.TestProjection> search = new Search.Builder<QueryDSLSearchTest.TestProjection>()
                .query(query)
                .sortingMap(sortingMap)
                .pageable(PageRequest.of(page, pageSize))
                .build();

        search.getResults();

        int expectedOffset = page * pageSize;
        int expectedLimit = expectedOffset + pageSize;
        Mockito.verify(query, Mockito.times(1)).offset(expectedOffset);
        Mockito.verify(query, Mockito.times(1)).limit(expectedLimit);
        Mockito.verify(query, Mockito.times(1)).fetch();
    }

    @Test
    public void calling_getResults_method_of_a_Search_with_sorting_should_apply_pagination_and_sorting_to_the_query_and_fetch_the_query_results() {
        JPAQuery<TestProjection> query = Mockito.mock(JPAQuery.class);
        int page = 0;
        int pageSize = 10;
        Search<TestProjection> search = new Search.Builder<TestProjection>()
                .query(query)
                .sortingMap(sortingMap)
                .pageable(PageRequest.of(
                        page,
                        pageSize,
                        Sort.by(Sort.Direction.ASC, testSortProperty)))
                .build();

        search.getResults();

        int expectedOffset = page * pageSize;
        int expectedLimit = expectedOffset + pageSize;
        OrderSpecifier expectedOrderSpecifier = new OrderSpecifier(Order.ASC, QAccount.account.identifier);
        Mockito.verify(query, Mockito.times(1)).offset(expectedOffset);
        Mockito.verify(query, Mockito.times(1)).limit(expectedLimit);
        Mockito.verify(query, Mockito.times(1)).orderBy(expectedOrderSpecifier);
        Mockito.verify(query, Mockito.times(1)).fetch();
    }
}
