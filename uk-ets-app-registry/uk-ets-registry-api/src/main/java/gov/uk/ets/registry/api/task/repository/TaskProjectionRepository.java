package gov.uk.ets.registry.api.task.repository;

import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.task.shared.TaskProjection;
import gov.uk.ets.registry.api.task.shared.TaskSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Repository for tasks.
 */
public interface TaskProjectionRepository {

    /**
     * Searches for tasks according to the passed criteria and pageable and returns
     * the {@link Page} of results.
     *
     * @param criteria the task search criteria
     * @param pageable pagination input
     * @return
     */
    Page<TaskProjection> adminSearch(TaskSearchCriteria criteria, Pageable pageable);

    /**
     * Searches for tasks according to the passed criteria and pageable and returns
     * the {@link Page} of results.
     *
     * @param criteria the task search criteria
     * @param pageable pagination input
     * @return
     */
    Page<TaskProjection> userSearch(TaskSearchCriteria criteria, Pageable pageable);

    /**
     * Expose the admin query needed for the reports.
     */
    JPAQuery<TaskProjection> getAdminQuery(TaskSearchCriteria criteria);
    
    /**
     * Expose the user query needed for the reports.
     */
    JPAQuery<TaskProjection> getUserQuery(TaskSearchCriteria criteria);
}
