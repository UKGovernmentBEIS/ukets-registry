package gov.uk.ets.registry.api.task.web.mappers;

import gov.uk.ets.registry.api.common.search.PageableMapper;
import gov.uk.ets.registry.api.common.search.PageParameters;
import gov.uk.ets.registry.api.task.shared.TaskPropertyPath;
import gov.uk.ets.registry.api.task.shared.TaskSearchAliases;
import gov.uk.ets.registry.api.task.web.mappers.TaskSearchPageableMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TaskSearchPageableMapperTest {

    TaskSearchPageableMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new TaskSearchPageableMapper();
    }

    @Test
    public void TaskSearchPageableMapper_should_map_empty_page_parameters_to_default_pageable() {
        Pageable defaultPageable = PageRequest.of(
                PageableMapper.ZERO_PAGE.intValue(),
                PageableMapper.MAX_RESULTS_PER_PAGE.intValue(),
                Sort.by(Sort.Direction.ASC, TaskPropertyPath.TASK_REQUEST_ID));

        PageParameters emptyPageParams = getEmptyPageParams();

        Pageable returnedPageable = mapper.get(emptyPageParams);

        assertThat(returnedPageable, is(defaultPageable));
    }

    @Test
    public void TaskSearchPageableMapper_should_pass_the_page_parameters_to_the_mapped_pageable() {
        PageParameters params = new PageParameters();
        params.setSortDirection(Direction.ASC);
        params.setPage(3);
        params.setPageSize(10L);

        Pageable pageable = mapper.get(params);

        assertThat(pageable.getPageNumber(), is(params.getPage()));
        assertThat(pageable.getPageSize(), is(params.getPageSize().intValue()));
    }

    @Test
    public void TaskSearchPageableMapper_should_return_a_pageable_with_sort_properties_the_corresponded_to_the_sortField_param_entity_property_paths() {
        final Map<String, String[]> map = getSortFieldParamToPropertyPathMap();
        map.keySet().forEach(key -> {
            PageParameters params = new PageParameters();
            params.setSortDirection(Direction.ASC);
            params.setSortField(key);
            Sort expectedSort = Sort.by(Direction.ASC, map.get(key));
            Sort returnedSort = mapper.get(params).getSort();

            assertThat(returnedSort, is(expectedSort));
        });
    }

    private PageParameters getEmptyPageParams() {
        PageParameters emptyPageParams = new PageParameters();
        emptyPageParams.setPageSize(null);
        emptyPageParams.setPage(null);
        emptyPageParams.setSortDirection(Direction.ASC);
        emptyPageParams.setSortField(null);

        return emptyPageParams;
    }

    private Map<String, String[]> getSortFieldParamToPropertyPathMap() {
        return Stream.of(
                new AbstractMap.SimpleEntry<>(
                        TaskSearchPageableMapper.SortFieldParam.REQUEST_ID.getName(),
                        new String[]{TaskPropertyPath.TASK_REQUEST_ID}),
                new AbstractMap.SimpleEntry<>(
                        TaskSearchPageableMapper.SortFieldParam.TASK_TYPE.getName(),
                        new String[]{TaskPropertyPath.TASK_TYPE}),
                new AbstractMap.SimpleEntry<>(
                        TaskSearchPageableMapper.SortFieldParam.INITIATOR.getName(),
                        new String[] {
                                TaskPropertyPath.TASK_INITIATOR_FIRST_NAME,
                                TaskPropertyPath.TASK_INITIATOR_LAST_NAME
                        }),
                new AbstractMap.SimpleEntry<>(
                        TaskSearchPageableMapper.SortFieldParam.CLAIMANT.getName(),
                        new String[] {
                                TaskPropertyPath.TASK_CLAIMANT_FIRST_NAME,
                                TaskPropertyPath.TASK_CLAIMANT_LAST_NAME
                        }),
                new AbstractMap.SimpleEntry<>(
                        TaskSearchPageableMapper.SortFieldParam.ACCOUNT_NUMBER.getName(),
                        new String[]{TaskPropertyPath.ACCOUNT_IDENTIFIER}),
                new AbstractMap.SimpleEntry<>(
                        TaskSearchPageableMapper.SortFieldParam.ACCOUNT_TYPE.getName(),
                        new String[]{TaskPropertyPath.ACCOUNT_KYOTO_TYPE}),
                new AbstractMap.SimpleEntry<>(
                        TaskSearchPageableMapper.SortFieldParam.TRANSACTION_ID.getName(),
                        new String[]{TaskSearchAliases.TRANSACTION_IDENTIFIERS.getAlias()}),
                new AbstractMap.SimpleEntry<>(
                        TaskSearchPageableMapper.SortFieldParam.CREATED_ON.getName(),
                        new String[]{TaskPropertyPath.TASK_CREATED_DATE}),
                new AbstractMap.SimpleEntry<>(
                        TaskSearchPageableMapper.SortFieldParam.TASK_STATUS.getName(),
                        new String[]{TaskPropertyPath.TASK_STATUS}),
                new AbstractMap.SimpleEntry<>(
                        TaskSearchPageableMapper.SortFieldParam.ACCOUNT_HOLDER.getName(),
                        new String[]{TaskPropertyPath.ACCOUNT_HOLDER_NAME}))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}