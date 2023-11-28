package gov.uk.ets.reports.generator.kyotoprotocol.sef.util;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by gkountak on 14/7/2015.
 */
public class CalendarUtils {
    /**
     * Gets a new {@link javax.xml.datatype.XMLGregorianCalendar}
     * @return
     */
    public static XMLGregorianCalendar newXmlGregorianCalendar() {
        GregorianCalendar c = new GregorianCalendar();
        XMLGregorianCalendar xmlGregorianCalendar;
        try {
            xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException();
        }

        return xmlGregorianCalendar;
    }

    /**
     * Gets a new {@link javax.xml.datatype.XMLGregorianCalendar}
     * @return
     */
    public static XMLGregorianCalendar newXmlGregorianCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        GregorianCalendar c = new GregorianCalendar();
        XMLGregorianCalendar xmlGregorianCalendar;
        try {
            xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            xmlGregorianCalendar.setDay(cal.get(Calendar.DAY_OF_MONTH));
            xmlGregorianCalendar.setMonth(cal.get(Calendar.MONTH)+1);
            xmlGregorianCalendar.setYear(cal.get(Calendar.YEAR));
            xmlGregorianCalendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException();
        }

        return xmlGregorianCalendar;
    }
}
