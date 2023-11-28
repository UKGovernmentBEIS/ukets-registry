package gov.uk.ets.registry.api.tal.web.model.search;

import gov.uk.ets.registry.api.common.search.PageParameters;
import gov.uk.ets.registry.api.common.search.PageableMapper;
import org.springframework.data.domain.Sort;

import java.util.function.Function;

/**
 * Mapper which responsibility is to map the received {@link PageParameters} params to a {@link
 * org.springframework.data.domain.Pageable} object
 */

public class TALSearchPageableMapper extends PageableMapper {

    public TALSearchPageableMapper() {
        super(SortFieldParam.values(), TALSearchPageableMapper.SortFieldParam.ACCOUNT_FULL_IDENTIFIER);
    }

    /**
     * The Sort field param enum. It maps a sort filed request parameter to a {@link Sort} object.
     */
    public enum SortFieldParam implements SortParameter {
        // Dummy sort parameters enum to ignore dynamic orderBy build
        ACCOUNT_FULL_IDENTIFIER("accountFullIdentifier", direction ->
            Sort.by(direction, "accountFullIdentifier")),
        TRUSTED_ACCOUNT_STATUS("status", direction ->
            Sort.by(direction, "status")),
        TRUSTED_ACCOUNT_TYPE("underSameAccountHolder", direction ->
            Sort.by(direction, "underSameAccountHolder"));

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
