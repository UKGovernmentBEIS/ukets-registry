package gov.uk.ets.reports.generator.kyotoprotocol.commons.util;

import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.ConfigLoader;
import lombok.extern.log4j.Log4j2;

import java.sql.*;

/**
 * @author gkountak
 *
 */
@Log4j2
public final class DBConnect {

    private DBConnect(){ }

    public static final Connection getConnection(String connectionString, String username, String password)
            throws SQLException {
        /* create something like this: "192.168.2.1:1521:X01A" */

        Connection con = DriverManager.getConnection(connectionString, username, password);
        log.debug("Acquired JDBC connection to: " + connectionString);
        return con;
    }

    public static void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            log.warn("An SQLException was raised while closing the connection: " + e.getMessage());
        }
    }

    /**
     * Closes the db connection.
     * @param con
     * @param ps
     */
    public static void close(Connection con, PreparedStatement ps) {
        log.debug("Releasing DB resources.");
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            log.warn("Prepared statement could not be closed.", e);
        }

        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            log.warn("Connection could not be closed.", e);
        }
    }

    /**
     * Handles the closing of the {@link PreparedStatement}
     * @param ps The PreparedStatement to close
     */
    public static void close(PreparedStatement ps) {
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException se) {
            log.error("Error closing the statement", se);
        }
    }

    /**
     * Handles the closing of the {@link ResultSet}
     * @param rs The ResultSet to close
     */
    public static void close(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException se) {
            log.error("Error closing the result set", se);
        }
    }

    /**
     * Sets the statement fetch size to the specified value
     * @param statement
     * @param fetchSize
     */
    public static void setFetchSize(Statement statement, Integer fetchSize) {
        if(fetchSize != null) {
            try {
                statement.setFetchSize(fetchSize);
            } catch (SQLException e) {
                log.error("An exception occurred while trying to set the fetch size of statement.", e);
            }
        }
    }

    /**
     * Sets the statement fetch size to the configured value
     * @param statement
     */
    public static void setFetchSizeToConfiguredValue(Statement statement) {
        Integer fetchSize = ConfigLoader.getConfigLoader().getDbQueryFetchSize();
        setFetchSize(statement, fetchSize);
    }

}