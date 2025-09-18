package gov.uk.ets.registry.api.itl.message.repository;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;

import gov.uk.ets.registry.api.common.search.OptionalBooleanBuilder;
import gov.uk.ets.registry.api.common.search.Search;
import gov.uk.ets.registry.api.common.search.SearchUtils;
import gov.uk.ets.registry.api.itl.message.domain.AcceptMessageLog;
import gov.uk.ets.registry.api.itl.message.domain.QAcceptMessageLog;
import gov.uk.ets.registry.api.itl.message.web.model.ITLMessagePropertyPath;
import gov.uk.ets.registry.api.itl.message.web.model.ITLMessageSearchCriteria;

public class ITLMessageSearchRepositoryImpl implements ITLMessageSearchRepository {

	private static final QAcceptMessageLog acceptMessageLog = QAcceptMessageLog.acceptMessageLog;

	@PersistenceContext
	EntityManager entityManager;

	private Map<String, EntityPathBase<?>> sortingMap = Stream
			.of(new Object[][] { { ITLMessagePropertyPath.MESSAGE_IDENTIFIER, acceptMessageLog },
					{ ITLMessagePropertyPath.MESSAGE_FROM, acceptMessageLog },
					{ ITLMessagePropertyPath.MESSAGE_TO, acceptMessageLog },
					{ ITLMessagePropertyPath.MESSAGE_DATE, acceptMessageLog },
					{ ITLMessagePropertyPath.MESSAGE_CONTENT, acceptMessageLog } })
			.collect(Collectors.toMap(data -> (String) data[0], data -> (EntityPathBase<?>) data[1]));

	private JPAQuery<AcceptMessageLog> getQuery(ITLMessageSearchCriteria criteria) {
		JPAQuery<AcceptMessageLog> query = new JPAQuery<AcceptMessageLog>(entityManager).from(acceptMessageLog);

		return query.where(new OptionalBooleanBuilder(acceptMessageLog.isNotNull())
				.notNullAnd(acceptMessageLog.id::eq, criteria.getMessageId())
				.notNullAnd(this::getMessageDateFrom, criteria.getMessageDateFrom())
				.notNullAnd(this::getMessageDateTo, criteria.getMessageDateTo()).build());
	}

	@Override
	public Page<AcceptMessageLog> search(ITLMessageSearchCriteria criteria, Pageable pageable) {
		return new Search.Builder<AcceptMessageLog>().pageable(pageable).sortingMap(sortingMap)
				.query(getQuery(criteria)).build().getResults();
	}

	private BooleanExpression getMessageDateFrom(Date messageDateFrom) {
		return SearchUtils.getFromDatePredicate(messageDateFrom, acceptMessageLog.messageDatetime);
	}

	private BooleanExpression getMessageDateTo(Date messageDateTo) {
		return SearchUtils.getUntilDatePredicate(messageDateTo, acceptMessageLog.messageDatetime);
	}
}
