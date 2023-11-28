package gov.uk.ets.registry.api.itl.message.web.model;

import java.util.function.Function;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import gov.uk.ets.registry.api.common.search.PageableMapper.SortParameter;

public enum ITLMessageSortFieldParam implements SortParameter {

	  MESSAGE_IDENTIFIER("messageId", direction -> Sort.by(direction, ITLMessagePropertyPath.MESSAGE_IDENTIFIER)),
	  MESSAGE_FROM("from", direction -> Sort.by(direction, ITLMessagePropertyPath.MESSAGE_FROM)),
	  MESSAGE_TO("to", direction -> Sort.by(direction, ITLMessagePropertyPath.MESSAGE_TO)),
	  MESSAGE_DATE("messageDate", direction -> Sort.by(direction, ITLMessagePropertyPath.MESSAGE_DATE)),
	  MESSAGE_CONTENT("content", direction -> Sort.by(direction, ITLMessagePropertyPath.MESSAGE_CONTENT));
	  
	private String sortField;

	private Function<Direction, Sort> getSortFunc;

	ITLMessageSortFieldParam(String sortField, Function<Sort.Direction, Sort> getSortFunc) {
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
