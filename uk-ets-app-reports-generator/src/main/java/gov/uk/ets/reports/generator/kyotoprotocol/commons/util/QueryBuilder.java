package gov.uk.ets.reports.generator.kyotoprotocol.commons.util;

import lombok.extern.log4j.Log4j2;

import java.io.*;

/**
 * @author gkountak
 *
 */
@Log4j2
public class QueryBuilder {

    /**
     * Returns a query stored in a file with all new lines replaced by a space.
     *
     * @param filename
     * @return
     * @throws FileNotFoundException
     */
    public String getQueryFromFileStripNewlines(String filename) throws FileNotFoundException {
        InputStream is = QueryBuilder.class.getResourceAsStream(filename);
        if (is == null) {
            throw new FileNotFoundException(filename + " could not be found!");
        }
        BufferedReader br = null;
        String query = null;
        try {
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                /*
                 * Usually here the new line is appended but in order to get a
                 * query ready for execution I chose to replace the newline with
                 * space.
                 */
                sb.append(" ");
                line = br.readLine();
            }
            query = sb.toString();
        } catch (IOException e) {
            log.warn("Problem while reading file: " + filename, e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                log.warn("Cannot close file: " + filename, e);
            }
        }
        return query;
    }

    /**
     * Returns a query stored in a file.
     *
     * @param filename
     * @return
     * @throws FileNotFoundException
     */
    public String getQueryFromFile(String filename) throws FileNotFoundException {
        InputStream is = QueryBuilder.class.getResourceAsStream(filename);
        if (is == null) {
            throw new FileNotFoundException(filename + " could not be found!");
        }
        BufferedReader br = null;
        String query = null;
        try {
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);

                sb.append("\n");
                line = br.readLine();
            }
            query = sb.toString();
        } catch (IOException e) {
            log.warn("Problem while reading file: " + filename, e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                log.warn("Cannot close file: " + filename, e);
            }
        }
        return query;
    }
}

