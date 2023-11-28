package gov.uk.ets.registry.api.common.search;

import gov.uk.ets.registry.api.task.shared.TaskPropertyPath;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * General Mapper which responsibility is to map the received {@link PageParameters} params to a
 * {@link Pageable} object
 */
public class PageableMapper {

  /**
   * The first page of results
   */
  public static final Integer ZERO_PAGE = 0;
  /**
   * The max size of page
   */
  public static final Long MAX_RESULTS_PER_PAGE = 10000L;

  private SortParameter[] sortParameters;

  private SortParameter defaultSortParameter;

  /**
   * Constructor
   *
   * @param sortParameters       The array of the available {@link SortParameter} implementations
   * @param defaultSortParameter The {@link SortParameter} which is responsible for the sorting by
   *                          default
   */
  public PageableMapper(SortParameter[] sortParameters, SortParameter defaultSortParameter) {
    this.sortParameters = sortParameters;
    this.defaultSortParameter = defaultSortParameter;
  }

  /**
   * Maps a {@link PageParameters} object to {@link Pageable} object.
   * <ul>
   *   <li>
   *     If no page info found in {@link PageParameters} argument, the default value of page returned
   *     in {@link Pageable} is {@link PageableMapper#ZERO_PAGE}
   *   </li>
   *   <li>
   *     If no page size info found in {@link PageParameters} argument, the default value of page
   *     size returned in {@link Pageable} is {@link PageableMapper#MAX_RESULTS_PER_PAGE}
   *   </li>
   *   <li>
   *     If no sorting info found in {@link PageParameters} argument,
   *     the default value of {@link Sort} object returned in {@link Pageable} is a {@link Sort} with sort
   *     property the {@link TaskPropertyPath#TASK_REQUEST_ID}
   *   </li>
   * </ul>
   * If no paging info found  then it returns a  {@link Pageable}
   * with page equals with {@link PageableMapper#ZERO_PAGE}, page size equals
   * with {@link PageableMapper#MAX_RESULTS_PER_PAGE}
   *
   * @param params The {@link PageParameters} object
   * @return {@link Pageable} object
   */
  public Pageable get(PageParameters params) {
    int page = Optional.ofNullable(params.getPage()).orElse(ZERO_PAGE).intValue();
    int perPage = Optional.ofNullable(params.getPageSize()).orElse(MAX_RESULTS_PER_PAGE)
        .intValue();
    Optional<SortParameter> optional = Stream.of(sortParameters)
        .filter(sortParameter -> sortParameter.getName().equals(params.getSortField())).findFirst();
    SortParameter sortMapper = optional.isPresent() ? optional.get() : defaultSortParameter;
    Sort sort = sortMapper.getSortProvider().apply(params.getSortDirection());
    return PageRequest.of(page, perPage, sort);
  }

  /**
   * Interface for sort parameters.
   */
  public interface SortParameter {

    /**
     * @return The name of the parameter
     */
    String getName();

    /**
     * @return {@link Function} which returns the {@link Sort} for the {@link Sort.Direction}
     * argument.
     */
    Function<Sort.Direction, Sort> getSortProvider();
  }
}
