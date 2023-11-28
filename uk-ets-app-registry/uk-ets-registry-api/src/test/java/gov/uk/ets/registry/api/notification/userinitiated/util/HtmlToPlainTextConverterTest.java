package gov.uk.ets.registry.api.notification.userinitiated.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HtmlToPlainTextConverterTest {


    @Test
    void shouldConvertToPlainText() {
        String body =
            ("<p><strong>Account Holder: ${accountHolder.name}</strong></p><p><strong>Account ID: ${accountId}</strong></p><p><strong>Permit ID: ${installation.permitId} </strong></p><p><strong>Installation: ${installation.name}</strong></p><p><br></p><ul><li><strong>a</strong></li><li><strong>b</strong></li><li><strong>c</strong></li></ul><p><br></p><ol><li><strong>d</strong></li><li><strong>e</strong></li><li><strong>f</strong></li></ol><p><br></p><p>Dear ${user.firstName} ${user.lastName},</p><p><br></p><p><strong>Your reportable emissions for 2021 must be entered into your Registry account by 31 March ${currentYear}</strong></p><p><br></p><p>This notification is being sent to you by the Registry Administrator in your capacity as an Authorised Representative of ${accountId}}.</p><p><br></p><p>In the UK ETS, an Operator is required to submit a verified report of the reportable emissions of its installation for each Scheme Year to its Regulator in ETSWAP by 31 March in the following year. The Regulator then provides this verified reportable emissions figure to the Registry Administrator, so that it can be entered into the Operator’s Operator Holding Account (OHA) in the UK ETS Registry by 30 April. </p><p><br></p><p>As of the date of this notification ${currentDate}, your Regulator has not yet provided your verified reportable emissions figure to the Registry Administrator for entry into your OHA. The fact that your verified reportable emissions figure has not been entered into your OHA does not affect your statutory obligation to surrender a number of allowances equal to your reportable emissions by 30 April, or your ability to perform the surrender transaction in the UK ETS Registry. If an Operator fails to surrender sufficient allowances it may be liable to pay a penalty, including the mandatory Excess Emissions Penalty of £100 (index linked) for each allowance that it failed to surrender.</p><p><br></p><p>If you have not yet submitted your verified emissions report to your Regulator, please ensure you do so by the deadline of 31 March. If you have submitted your verified emissions report, but are unsure how many allowances you need to surrender, please contact your Regulator as soon as possible.</p><p><br></p><p>If you are having any difficulties accessing your OHA in the UK ETS Registry or are experiencing any other issues with the Registry system, please contact the UK Registry Team </p><p>by emailing the UK Registry Helpdesk at <a href=\"mailto:etregistryhelp@environment-agency.gov.uk\" rel=\"noopener noreferrer\" target=\"_blank\">etregistryhelp@environment-agency.gov.uk</a></p><p> (available on Monday to Friday from 9am to 5pm, except for UK public holidays).  </p><p><br></p><p>Kind Regards,</p><p><br></p><p>The UK Registry Team</p><p class=\"ql-align-justify\"><br></p>");

        String plainText = HtmlToPlainTextConverter.convertHtmlToPlainText(body);

        assertThat(plainText).isNotNull();

        assertThat(plainText).contains(
            "at etregistryhelp@environment-agency.gov.uk \n" +
                "<mailto:etregistryhelp@environment-agency.gov.uk>"
        );

        assertThat(plainText).contains(
            " * a\n" +
                " * b\n" +
                " * c\n" +
                "\n" +
                "\n" +
                "\n" +
                " * d\n" +
                " * e\n" +
                " * f"
        );
    }
}
