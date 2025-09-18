package gov.uk.ets.registry.api.helper.persistence;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TokenizeHelper {

    private static final String SELECT_ = "select ";
    private static final String COMMA = ",";
    private static final String SPACE_CHAR = " ";
    private static final String FULL_STOP = ".";
    private static final String FROM = " from ";

    /**
     * Tokenize select clause
     *
     * @param clause select clause
     * @return the select clause tokens
     */
    public static List<String> tokenizeClause(String clause) {
        int startSelect = clause.indexOf(SELECT_);
        int endSelect = startSelect + SELECT_.length();
        String clauseWithoutSelect = clause.substring(endSelect);
        String clauseWithoutCommas = clauseWithoutSelect.replaceAll(COMMA, SPACE_CHAR);
        List<String> columnsWithPrefix =
            Arrays.stream(clauseWithoutCommas.split(SPACE_CHAR))
                .filter(s -> s.contains(FULL_STOP))
                .collect(Collectors.toList());
        return columnsWithPrefix.stream()
            .map(s -> {
                int indexOfDot = s.indexOf(FULL_STOP);
                return s.substring(indexOfDot+1);
            }).collect(Collectors.toList());
    }

    public static String extractSelectClause(String query) {
        int selectIndex = query.indexOf(SELECT_);
        int fromIndex = query.indexOf(FROM);
        return query.substring(selectIndex, fromIndex);
    }
}
