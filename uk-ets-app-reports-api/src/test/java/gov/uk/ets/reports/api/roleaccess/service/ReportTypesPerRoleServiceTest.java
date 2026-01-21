package gov.uk.ets.reports.api.roleaccess.service;

import static org.assertj.core.api.Assertions.assertThat;

import gov.uk.ets.reports.model.ReportRequestingRole;
import gov.uk.ets.reports.model.ReportType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = ReportTypesPerRoleService.class)
public class ReportTypesPerRoleServiceTest {


    @Autowired
    private ReportTypesPerRoleService service;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void shouldListReportTypesForAdminAndAuthorityAndAr() {
        List<ReportType> reportsForAdmin = service.getReportTypes(ReportRequestingRole.administrator);
        List<ReportType> reportsForAuthority = service.getReportTypes(ReportRequestingRole.authority);
        List<ReportType> reportsForAr = service.getReportTypes(null);

        assertThat(reportsForAdmin).hasSize(35);
        assertThat(reportsForAuthority).hasSize(14);
        assertThat(reportsForAr).isEmpty();
    }

    @Test
    public void shouldCheckIfReportTypeIsAvailableForRole() {
        boolean type1 = service.availableTypesForRole(ReportType.R0010, ReportRequestingRole.administrator);
        boolean type2 = service.availableTypesForRole(ReportType.R0015, ReportRequestingRole.administrator);
        boolean type3 = service.availableTypesForRole(ReportType.R0015, ReportRequestingRole.authority);

        assertThat(type1).isTrue();
        assertThat(type2).isFalse();
        assertThat(type3).isTrue();
    }
}
