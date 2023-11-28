package gov.uk.ets.reports.generator.kyotoprotocol.commons.util;

import java.io.*;
import java.text.ParseException;
import java.util.Collection;

import gov.uk.ets.reports.generator.kyotoprotocol.KyotoReportOutcome;
import gov.uk.ets.reports.generator.kyotoprotocol.KyotoReportOutcome.KyotoReportContentType;
import  gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission;
import lombok.extern.log4j.Log4j2;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

/**
 * @author gkountak
 *
 */
@Log4j2
public class CreateXML {


    /**
     * Creates an array of XML reports and stores them in the file system.
     * @param directory
     * @param sefSubmissions
     * @return File[]
     */
    public static KyotoReportOutcome [] createReports(Collection<SEFSubmission> sefSubmissions) {

        KyotoReportOutcome[] files = new KyotoReportOutcome[sefSubmissions.size()];

        int i = 0;
        for (SEFSubmission sefSub : sefSubmissions) {
            files[i++] = createXMLReport(sefSub);
        }

        return files;
    }

    /**
     * Creates an array of XML reports and stores them in the file system.
     * @param directory
     * @param sefSubmissions
     * @return File[]
     */
    public static KyotoReportOutcome[] createSef2Reports(Collection< gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission> sefSubmissions) {

        KyotoReportOutcome[] files = new KyotoReportOutcome[sefSubmissions.size()];

        int i = 0;
        for ( gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission sefSub : sefSubmissions) {
            files[i++] = createXMLReport(sefSub);
        }

        return files;
    }

    /**
     * Creates an XML report, stores it in the disk and returns a File handle.
     * @param directory
     * @param sefSubmission
     * @return File
     */
    public static KyotoReportOutcome createXMLReport(SEFSubmission sefSubmission) {

        KyotoReportOutcome file = null;
        
        try(StringWriter stringWriter = new StringWriter()) {
            JAXBContext jaxbContext = JAXBContext.newInstance(SEFSubmission.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(sefSubmission, stringWriter);
            
            file = KyotoReportOutcome
            .builder()
            .content(stringWriter.toString().getBytes())
            .fileName(HeaderUtils.concatFileName(sefSubmission.getHeader()) + ".xml")
            .contentType(KyotoReportContentType.TEXT_XML)
            .build();
        } catch (PropertyException e) {
            log.fatal(e);
        } catch (JAXBException e) {
            log.fatal(e);
        } catch (ParseException e) {
            log.fatal(e);
        } catch (IOException e) {
            log.fatal(e);
        }
        return file;
    }

    /**
     * Creates an XML report, stores it in the disk and returns a File handle.
     * @param directory
     * @param sefSubmission
     * @return File
     */
    public static KyotoReportOutcome createXMLReport(gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission sefSubmission) {

        KyotoReportOutcome file = null;
        
        try (StringWriter stringWriter = new StringWriter()) {
            
            JAXBContext jaxbContext = JAXBContext.newInstance( gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(sefSubmission, stringWriter);
            
            file = KyotoReportOutcome
            .builder()
            .content(stringWriter.toString().getBytes())
            .fileName(HeaderUtils.concatFileName(sefSubmission.getHeader()) + ".xml")
            .contentType(KyotoReportContentType.TEXT_XML)
            .build();
        } catch (PropertyException e) {
            log.fatal(e);
        } catch (JAXBException e) {
            log.fatal(e);
        } catch (ParseException e) {
            log.fatal(e);
        } catch (IOException e) {
            log.fatal(e);
        }
        return file;
    }
}
