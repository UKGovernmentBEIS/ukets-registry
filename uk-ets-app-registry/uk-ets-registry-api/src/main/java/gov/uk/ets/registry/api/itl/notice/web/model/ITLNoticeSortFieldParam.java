package gov.uk.ets.registry.api.itl.notice.web.model;

import gov.uk.ets.registry.api.common.search.PageableMapper.SortParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.function.Function;

public enum ITLNoticeSortFieldParam implements SortParameter {
	NOTICE_IDENTIFIER("notificationIdentifier", direction -> Sort.by(direction, ITLNoticePropertyPath.NOTICE_IDENTIFIER)),
	NOTICE_DATE_RECEIVED_ON("receivedOn", direction -> Sort.by(direction, ITLNoticePropertyPath.NOTICE_DATE_RECEIVED_ON)),
	NOTICE_DATE_CREATED_ON("lastUpdateOn", direction -> Sort.by(direction, ITLNoticePropertyPath.NOTICE_DATE_UPDATED_ON)),
	NOTICE_TYPE("type", direction -> Sort.by(direction, ITLNoticePropertyPath.NOTICE_TYPE)),
	NOTICE_STATUS("status", direction -> Sort.by(direction, ITLNoticePropertyPath.NOTICE_STATUS));

	private String sortField;

	private Function<Direction, Sort> getSortFunc;

	ITLNoticeSortFieldParam(String sortField, Function<Direction, Sort> getSortFunc) {
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
	public Function<Direction, Sort> getSortProvider() {
		return getSortFunc;
	}

}
