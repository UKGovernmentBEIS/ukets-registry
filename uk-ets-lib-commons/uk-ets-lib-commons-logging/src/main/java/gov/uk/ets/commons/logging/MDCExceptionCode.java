package gov.uk.ets.commons.logging;


import static gov.uk.ets.commons.logging.ErrorCode.DB_BATCH_UPDATE;
import static gov.uk.ets.commons.logging.ErrorCode.DB_PERSISTENCE;
import static gov.uk.ets.commons.logging.ErrorCode.DB_PSQL;
import static gov.uk.ets.commons.logging.ErrorCode.ETS_CLAMAV;
import static gov.uk.ets.commons.logging.ErrorCode.ETS_TX_EXEC;
import static gov.uk.ets.commons.logging.ErrorCode.ILLEGAL_ARG;
import static gov.uk.ets.commons.logging.ErrorCode.JDBC_CONNECTION;
import static gov.uk.ets.commons.logging.ErrorCode.JWT_TOKEN_EXPIRED;
import static gov.uk.ets.commons.logging.ErrorCode.KAFKA_NOT_LEADER_FOLLOWER;
import static gov.uk.ets.commons.logging.ErrorCode.KAFKA_REBALANCE_IN_PROGRESS;
import static gov.uk.ets.commons.logging.ErrorCode.METHOD_NOT_SUPPORTED;
import static gov.uk.ets.commons.logging.ErrorCode.NPE;
import static gov.uk.ets.commons.logging.ErrorCode.RUNTIME;
import static gov.uk.ets.commons.logging.ErrorCode.SPRING_BIND;
import static gov.uk.ets.commons.logging.ErrorCode.SPRING_CANNOT_CREATE_TX;
import static gov.uk.ets.commons.logging.ErrorCode.SPRING_SEC_REQUEST_REJECTED;
import static gov.uk.ets.commons.logging.ErrorCode.SPRING_TX_DATA_INTEGRITY_VIOLATION;
import static gov.uk.ets.commons.logging.ErrorCode.SPRING_WEB_RESOURCE_ACCESS;
import static gov.uk.ets.commons.logging.ErrorCode.TOMCAT_CLIENT_ABORT;
import static gov.uk.ets.commons.logging.ErrorCode.TOMCAT_MALFORMED_STREAM;
import static gov.uk.ets.commons.logging.ErrorCode.TOMCAT_NO_RESPONSE;

import java.sql.BatchUpdateException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.kafka.common.errors.NotLeaderOrFollowerException;
import org.apache.kafka.common.errors.RebalanceInProgressException;
import org.apache.tomcat.util.http.fileupload.MultipartStream;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.client.ResourceAccessException;

/**
 * Maps the most common exceptions' canonical names to custom error codes.
 * If the class is not in the classpath, class' canonical name is hard-coded.
 */
enum MDCExceptionCode {
    // FALLBACK
    UNKNOWN("Unknown", ErrorCode.UNKNOWN),
    // JDK
    NullPointerException(NullPointerException.class.getCanonicalName(), NPE),
    IllegalArgumentException(IllegalArgumentException.class.getCanonicalName(), ILLEGAL_ARG),
    RuntimeException(RuntimeException.class.getCanonicalName(), RUNTIME),
    // SPRING
    HttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException.class.getCanonicalName(),
        METHOD_NOT_SUPPORTED),
    DataIntegrityViolationException(DataIntegrityViolationException.class.getCanonicalName(),
        SPRING_TX_DATA_INTEGRITY_VIOLATION),
    BindException(BindException.class.getCanonicalName(), SPRING_BIND),
    // TODO: this one bellow is thrown by spring's StrictHttpFirewall (early?) and our filter is never reached
    RequestRejectedException("org.springframework.security.web.firewall.RequestRejectedException",
        SPRING_SEC_REQUEST_REJECTED),
    CannotCreateTransactionException(CannotCreateTransactionException.class.getCanonicalName(),
        SPRING_CANNOT_CREATE_TX),
    // thrown when RestTemplate cannot access a service
    ResourceAccessException(ResourceAccessException.class.getCanonicalName(), SPRING_WEB_RESOURCE_ACCESS),
    // TOMCAT
    ClientAbortException(ClientAbortException.class.getCanonicalName(), TOMCAT_CLIENT_ABORT),
    MalformedStreamException(MultipartStream.MalformedStreamException.class.getCanonicalName(),
        TOMCAT_MALFORMED_STREAM),
    NoHttpResponseException("org.apache.http.NoHttpResponseException", TOMCAT_NO_RESPONSE),
    // DB - JPA
    PSQLException("org.postgresql.util.PSQLException", DB_PSQL),
    PersistenceException("javax.persistence.PersistenceException", DB_PERSISTENCE),
    BatchUpdateException(BatchUpdateException.class.getCanonicalName(), DB_BATCH_UPDATE),
    JDBCConnectionException("org.hibernate.exception.JDBCConnectionException", JDBC_CONNECTION),
    // KAFKA
    RebalanceInProgressException(RebalanceInProgressException.class.getCanonicalName(), KAFKA_REBALANCE_IN_PROGRESS),
    NotLeaderOrFollowerException(NotLeaderOrFollowerException.class.getCanonicalName(), KAFKA_NOT_LEADER_FOLLOWER),
    // CUSTOM
    TransactionExecutionException("gov.uk.ets.registry.api.transaction.exception.TransactionExecutionException",
        ETS_TX_EXEC),
    // OTHER
    // TODO this one is handled from advice, prabably not needed here
    ClamavException("gov.uk.ets.file.upload.error.ClamavException", ETS_CLAMAV),
    TokenExpiredException("com.auth0.jwt.exceptions.TokenExpiredException", JWT_TOKEN_EXPIRED);

    private final String classCanonicalName;
    private final ErrorCode errorCode;

    MDCExceptionCode(String classCanonicalName, ErrorCode errorCode) {
        this.classCanonicalName = classCanonicalName;
        this.errorCode = errorCode;
    }

    public static String retrieveErrorCodeForException(Throwable e) {
        String exceptionCanonicalName = e.getClass().getCanonicalName();
        
        String causeCanonicalName = null;
        if(Optional.ofNullable(e.getCause()).isPresent()) {
            causeCanonicalName = e.getCause().getClass().getCanonicalName();            
        }

        return fromCanonicalName(exceptionCanonicalName, causeCanonicalName);
    }

    /**
     * Check both the exception class and the cause class. For example for NestedServletExceptions
     * the actual useful exception is wrapped inside the cause.
     *
     * @param exCanonicalName    the canonical name of the original exception
     * @param causeCanonicalName the canonical name of the cause of the original exception
     */
    private static String fromCanonicalName(String exCanonicalName, String causeCanonicalName) {
        String errorCodes = Arrays.stream(values())
            .filter(exceptionCode ->
                exceptionCode.classCanonicalName.equals(exCanonicalName) ||
                    exceptionCode.classCanonicalName.equals(causeCanonicalName)
            )
            .map(MDCExceptionCode::getErrorCode)
            .map(ErrorCode::getCode)
            .collect(Collectors.joining(","));
        return !errorCodes.equals("") ? errorCodes : UNKNOWN.getErrorCode().getCode();
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}


