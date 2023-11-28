package gov.uk.ets.registry.api.itl.notice.web.model;

import gov.uk.ets.registry.api.common.search.PageableMapper.SortParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.function.Function;

public enum ITLNoticeDetailsSortFieldParam implements SortParameter {
	NOTICE_DETAILS_DATE("messageDate", direction -> Sort.by(direction, ITLNoticeDetailPropertyPath.MESSAGE_DATE)),
	NOTICE_DETAILS_CONTENT("content", direction -> Sort.by(direction, ITLNoticeDetailPropertyPath.CONTENT)),
	NOTICE_DETAILS_TYPE("type", direction -> Sort.by(direction, ITLNoticeDetailPropertyPath.TYPE)),
	NOTICE_DETAILS_STATUS("status", direction -> Sort.by(direction, ITLNoticeDetailPropertyPath.STATUS)),
	NOTICE_DETAILS_PROJECT_NUMBER("projectNumber", direction -> Sort.by(direction, ITLNoticeDetailPropertyPath.PROJECT_NUMBER)),
	NOTICE_DETAILS_UNIT_TYPE("unitType", direction -> Sort.by(direction, ITLNoticeDetailPropertyPath.UNIT_TYPE)),
	NOTICE_DETAILS_TARGET_VALUE("targetValue", direction -> Sort.by(direction, ITLNoticeDetailPropertyPath.TARGET_VALUE)),
	NOTICE_DETAILS_TARGET_DATE("targetDate", direction -> Sort.by(direction, ITLNoticeDetailPropertyPath.TARGET_DATE)),
	NOTICE_DETAILS_LULUCF_ACTIVITY("lulucfActivity", direction -> Sort.by(direction, ITLNoticeDetailPropertyPath.LULUCF_ACTIVITY)),
	NOTICE_DETAILS_COMMITMENT_PERIOD("commitPeriod", direction -> Sort.by(direction, ITLNoticeDetailPropertyPath.COMMIT_PERIOD)),
	NOTICE_DETAILS_ACTION_DUE_DATE("actionDueDate", direction -> Sort.by(direction, ITLNoticeDetailPropertyPath.ACTION_DUEDATE)),
	NOTICE_DETAILS_CREATION_DATE("createdDate", direction -> Sort.by(direction, ITLNoticeDetailPropertyPath.CREATED_DATE));

	private String sortField;

	private Function<Direction, Sort> getSortFunc;

	ITLNoticeDetailsSortFieldParam(String sortField, Function<Direction, Sort> getSortFunc) {
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
