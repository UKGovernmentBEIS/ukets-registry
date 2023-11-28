package gov.uk.ets.reports.generator;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Util {
    public static Long getNullableLong(ResultSet rs, String columnLabel) throws SQLException {
        long number = rs.getLong(columnLabel);
        return rs.wasNull() ? null : number;
    }

    public static Integer getNullableInteger(ResultSet rs, String columnLabel) throws SQLException {
        int number = rs.getInt(columnLabel);
        return rs.wasNull() ? null : number;
    }
}
