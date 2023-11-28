package gov.uk.ets.reports.api;

import gov.uk.ets.reports.api.domain.Report;
import gov.uk.ets.reports.model.ReportRequestingRole;
import gov.uk.ets.reports.model.ReportStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByGenerationDateBeforeAndStatusNot(LocalDateTime reportsExpirationInMinutes, ReportStatus status);

    List<Report> findByRequestDateBeforeAndStatus(LocalDateTime reportsExpirationInMinutes, ReportStatus status);
    
    List<Report> findByRequestingRole(ReportRequestingRole role, Sort sort);
    
    List<Report> findByRequestingUser(String urid, Sort sort);
}
