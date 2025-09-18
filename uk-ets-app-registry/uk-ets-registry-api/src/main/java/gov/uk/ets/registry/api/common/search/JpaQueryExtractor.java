package gov.uk.ets.registry.api.common.search;

import gov.uk.ets.reports.model.ReportQueryInfo;
import io.hypersistence.utils.common.ReflectionUtils;
import jakarta.persistence.Query;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.hibernate.query.spi.DomainQueryExecutionContext;
import org.hibernate.query.spi.QueryParameterBinding;
import org.hibernate.query.spi.QueryParameterBindings;
import org.hibernate.query.sqm.internal.ConcreteSqmSelectQueryPlan;
import org.hibernate.query.sqm.internal.DomainParameterXref;
import org.hibernate.query.sqm.internal.QuerySqmImpl;
import org.hibernate.query.sqm.tree.select.SqmSelectStatement;
import org.hibernate.sql.exec.spi.JdbcOperationQuerySelect;

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
        String nativeSql = buildNativeSql(query);

        if (nativeSql == null) {
            throw new RuntimeException("Could not extract sql statement.");
        }

        return nativeSql;
    }

    private static String buildNativeSql(Query query) {
        if(query instanceof QuerySqmImpl querySqm) {

            ConcreteSqmSelectQueryPlan selectQueryPlan = ReflectionUtils.invokeMethod(querySqm, "resolveSelectQueryPlan");

            Object cacheableSqmInterpretation = ReflectionUtils.getFieldValueOrNull(selectQueryPlan, "cacheableSqmInterpretation");
            if(cacheableSqmInterpretation == null) {
                cacheableSqmInterpretation = ReflectionUtils.invokeStaticMethod(
                    ReflectionUtils.getMethod(
                        ConcreteSqmSelectQueryPlan.class,
                        "buildCacheableSqmInterpretation",
                        SqmSelectStatement.class,
                        DomainParameterXref.class,
                        DomainQueryExecutionContext.class
                    ),
                    ReflectionUtils.getFieldValueOrNull(querySqm, "sqm"),
                    ReflectionUtils.getFieldValueOrNull(querySqm, "domainParameterXref"),
                    querySqm
                );
            }
            if (cacheableSqmInterpretation != null) {
                JdbcOperationQuerySelect
                    jdbcSelect = ReflectionUtils.getFieldValueOrNull(cacheableSqmInterpretation, "jdbcSelect");
                if (jdbcSelect != null) {
                    return applyParameters(querySqm.getQueryParameterBindings(), jdbcSelect.getSqlString());
                }
            }
        }

        return null;
    }

    private static String applyParameters(QueryParameterBindings queryParameterBindings, String sql) {
        int index = 1;
        while (sql.contains("?")) {
            QueryParameterBinding<Object> parameterBinding = queryParameterBindings.getBinding(index);
            if (parameterBinding.isMultiValued()) {
                for (Object parameter : parameterBinding.getBindValues()) {
                    sql = applyParameter(sql, parameter);
                }
            } else {
                sql = applyParameter(sql, parameterBinding.getBindValue());
            }
            index ++;
        }
        return sql;
    }

    private static String applyParameter(String sql, Object parameter) {
        return sql.replaceFirst("[?]", handleTextValues(parameter));
    }

    private static String handleTextValues(Object parameter) {
        if (parameter instanceof Number) {
            return parameter.toString();
        }
        return "'" + parameter + "'";
    }
}
