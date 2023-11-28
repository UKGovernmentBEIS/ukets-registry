package gov.uk.ets.registry.api.task.printenrolmentletter.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({"classpath:enrolment_letter.properties","classpath:application.properties"})
@Getter
public class PrintEnrolmentLetterConfig {
    @Value("${application.url}")
    private String applicationUrl;
    @Value("${image.logo.path}")
    private String logoPath;
    @Value("${enrolment.header.private}")
    private String headerPrivate;
    @Value("${enrolment.header.date}")
    private String headerDate;
    @Value("${enrolment.salutation}")
    private String salutation;
    @Value("${enrolment.paragraph1.title}")
    private String paragraph1Title;
    @Value("${enrolment.paragraph1}")
    private String paragraph1;
    @Value("${enrolment.paragraph1.moreInfo1}")
    private String paragraph1MoreInfo1;
    @Value("${enrolment.paragraph1.moreInfo2}")
    private String paragraph1MoreInfo2;
    @Value("${enrolment.paragraph2.title}")
    private String paragraph2Title;
    @Value("${enrolment.paragraph2}")
    private String paragraph2;
    @Value("${enrolment.paragraph2.moreInfo}")
    private String paragraph2MoreInfo;
    @Value("${enrolment.listItem1}")
    private String listItem1;
    @Value("${enrolment.listItem2}")
    private String listItem2;
    @Value("${enrolment.listItem3}")
    private String listItem3;
    @Value("${enrolment.listItem4}")
    private String listItem4;
    @Value("${enrolment.listItem5}")
    private String listItem5;
    @Value("${enrolment.paragraph3.title}")
    private String paragraph3Title;
    @Value("${enrolment.paragraph3}")
    private String paragraph3;
    @Value("${enrolment.paragraph3.moreInfo}")
    private String paragraph3MoreInfo;
    @Value("${enrolment.paragraph4.title}")
    private String paragraph4Title;
    @Value("${enrolment.paragraph4}")
    private String paragraph4;

}
