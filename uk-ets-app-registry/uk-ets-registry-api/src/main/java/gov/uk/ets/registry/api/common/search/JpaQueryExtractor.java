package gov.uk.ets.registry.api.common.search;

import com.zaxxer.hikari.pool.HikariProxyPreparedStatement;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.reports.model.ReportQueryInfo;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.persistence.Query;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.NoopLimitHandler;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.internal.AbstractSharedSessionContract;
import org.hibernate.loader.Loader;
import org.hibernate.loader.hql.QueryLoader;
import org.hibernate.query.internal.AbstractProducedQuery;
import org.hibernate.query.internal.QueryParameterBindingsImpl;

@UtilityClass
@Log4j2
public class JpaQueryExtractor {

    public static ReportQueryInfo extractReportQueryInfo(Query query) {
        return ReportQueryInfo.builder()
            .query(extractSqlString(query))
            .build();
    }

    /**
     * Retrieves the native sql query with bounded parameters, using reflection.
     */
    private static String extractSqlString(Query query) {
        try {
            AbstractProducedQuery abstractQuery = query.unwrap(AbstractProducedQuery.class);
            HQLQueryPlan hqlQueryPlan = getQueryPlan(abstractQuery);
            String nativeSqlUnbounded = getSqlUnboundedString(hqlQueryPlan);
            return getQueryFromPreparedStatement(abstractQuery, hqlQueryPlan, nativeSqlUnbounded);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            log.error("Could not retrieve PreparedStatement from query", e);
            throw new UkEtsException(e);
        }
    }

    /**
     * Unfortunately the HQLQueryPlan retrieved the way that Hibernate Types project  suggests is not always correct:
     * https://github.com/vladmihalcea/hibernate-types/blob/master/hibernate-types-52/src/main/java/com/vladmihalcea/hibernate/type/util/SQLExtractor.java
     * <p>
     * This is why we need to use reflection here too...
     */
    private static HQLQueryPlan getQueryPlan(AbstractProducedQuery abstractQuery)
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method getQueryParamBindingsMethod = AbstractProducedQuery.class.getDeclaredMethod("getQueryParameterBindings");
        getQueryParamBindingsMethod.setAccessible(true);
        QueryParameterBindingsImpl queryParameterBindings =
            (QueryParameterBindingsImpl) getQueryParamBindingsMethod.invoke(abstractQuery);
        String expandedQuery = queryParameterBindings
            .expandListValuedParameters(abstractQuery.getQueryString(), abstractQuery.getProducer());

        Method getQueryPlanMethod =
            AbstractSharedSessionContract.class.getDeclaredMethod("getQueryPlan", String.class, boolean.class);
        getQueryPlanMethod.setAccessible(true);
        return (HQLQueryPlan) getQueryPlanMethod.invoke(abstractQuery.getProducer(), expandedQuery, false);
    }


    private static String getSqlUnboundedString(HQLQueryPlan hqlQueryPlan) {
        String[] sqls = hqlQueryPlan
            .getSqlStrings();
        return sqls.length > 0 ? sqls[0] : null;
    }

    private static String getQueryFromPreparedStatement(AbstractProducedQuery abstractProducedQuery,
                                                        HQLQueryPlan hqlQueryPlan, String nativeSqlUnbounded)
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {

        QueryTranslatorImpl queryTranslator = getQueryTranslator(hqlQueryPlan);
        QueryLoader loader = getQueryLoader(queryTranslator);
        QueryParameters queryParameters = getQueryParameters(abstractProducedQuery);

        Method prepareQueryStatementMethod = Loader.class
            .getDeclaredMethod("prepareQueryStatement", String.class, QueryParameters.class, LimitHandler.class,
                boolean.class, SharedSessionContractImplementor.class);
        prepareQueryStatementMethod.setAccessible(true);
        HikariProxyPreparedStatement preparedStatement = (HikariProxyPreparedStatement) prepareQueryStatementMethod
            .invoke(loader, nativeSqlUnbounded, queryParameters, NoopLimitHandler.INSTANCE,
                false,
                abstractProducedQuery.getProducer());
        // HikariProxyPreparedStatement adds this prefix in the sql query string ...
        // there might be a better way to remove it but for no this will do.
        String prefixToRemove = String.format("%s@%s wrapping", preparedStatement.getClass().getSimpleName(),
            System.identityHashCode(preparedStatement));
        return preparedStatement.toString().replace(prefixToRemove, "");
    }

    private static QueryTranslatorImpl getQueryTranslator(HQLQueryPlan hqlQueryPlan)
        throws NoSuchFieldException, IllegalAccessException {
        Field translatorsField = HQLQueryPlan.class.getDeclaredField("translators");
        translatorsField.setAccessible(true);
        QueryTranslator[] queryTranslators = (QueryTranslator[]) translatorsField.get(hqlQueryPlan);
        return (QueryTranslatorImpl) queryTranslators[0];
    }

    private static QueryParameters getQueryParameters(AbstractProducedQuery abstractProducedQuery)
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method queryParamsMethod =
            AbstractProducedQuery.class.getDeclaredMethod("makeQueryParametersForExecution", String.class);
        queryParamsMethod.setAccessible(true);
        return (QueryParameters) queryParamsMethod
            .invoke(abstractProducedQuery, abstractProducedQuery.getQueryString());
    }

    private static QueryLoader getQueryLoader(QueryTranslatorImpl queryTranslator)
        throws NoSuchFieldException, IllegalAccessException {
        Field queryLoaderField = QueryTranslatorImpl.class.getDeclaredField("queryLoader");
        queryLoaderField.setAccessible(true);
        return (QueryLoader) queryLoaderField.get(queryTranslator);
    }
}
