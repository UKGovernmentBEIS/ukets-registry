package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.ComplianceManagementReportData;
import gov.uk.ets.reports.generator.domain.KeycloakUser;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.keycloak.KeycloakDbService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ComplianceManagementReportService implements ReportTypeService<ComplianceManagementReportData> {

    private final ReportDataMapper<ComplianceManagementReportData> mapper;
    private final KeycloakDbService keycloakDbService;

    @Override
    public ReportType reportType() {
        return ReportType.R0048;
    }

    @Override
    public List<Object> getReportDataRow(ComplianceManagementReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getAccountFullIdentifier());
        data.add(reportData.getAccountHolderId());
        data.add(reportData.getOperatorId());
        data.add(reportData.getPermitOrMonitoringPlanId());
        data.add(reportData.getAccountHolderName());
        data.add(reportData.getAccountName());
        data.add(reportData.getAccountType());
        data.add(reportData.getAccountOpeningDate());
        data.add(reportData.getAccountClosureDate());
        data.add(reportData.getAccountStatus());
        data.add(reportData.getRegulator());
        data.add(reportData.getFirstYearOfVerifiedEmissions());
        data.add(reportData.getLastYearOfVerifiedEmissions());
        data.add(reportData.getCumulativeEmissions());
        data.add(reportData.getCumulativeSurrenders());
        data.add(reportData.getSurrenderBalance());
        data.add(reportData.getCurrentSurrenderStatus());
        data.add(reportData.getValidatedARs());
        data.add(reportData.getEnrolledARs());
        data.add(reportData.getSuspendedARs());
        data.add(reportData.getTotalARs());
        data.add(reportData.getArChangesInProgress());
        data.add(reportData.getArNominationTaskIds());
        data.add(reportData.getArNominationTaskClaimants());
        data.add(reportData.getMostRecentSignIn());
        data.add(reportData.getTotalAvailableQuantity());
        data.add(reportData.getTotalReservedQuantity());
        data.add(reportData.getSurrenderTransactionPendingApproval());
        data.add(reportData.getReceiptOfAllowancesScheduled());
        data.add(reportData.getFourEyesTalTransactions());
        data.add(reportData.getTransactionsOffTheTalAllowed());
        data.add(reportData.getFourEyesSurrenderReturnOfExcessAllocation());
        data.add(reportData.getVerifiedEmissions2021());
        data.add(reportData.getVerifiedEmissions2022());
        data.add(reportData.getVerifiedEmissions2023());
        data.add(reportData.getVerifiedEmissions2024());
        data.add(reportData.getVerifiedEmissions2025());
        data.add(reportData.getVerifiedEmissions2026());
        data.add(reportData.getVerifiedEmissions2027());
        data.add(reportData.getVerifiedEmissions2028());
        data.add(reportData.getVerifiedEmissions2029());
        data.add(reportData.getVerifiedEmissions2030());
        data.add(reportData.getStaticSurrenderStatus2021());
        data.add(reportData.getStaticSurrenderStatus2022());
        data.add(reportData.getStaticSurrenderStatus2023());
        data.add(reportData.getStaticSurrenderStatus2024());
        data.add(reportData.getStaticSurrenderStatus2025());
        data.add(reportData.getStaticSurrenderStatus2026());
        data.add(reportData.getStaticSurrenderStatus2027());
        data.add(reportData.getStaticSurrenderStatus2028());
        data.add(reportData.getStaticSurrenderStatus2029());
        data.add(reportData.getStaticSurrenderStatus2030());
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List.of(
            "Account Number",
            "Account Holder ID",
            "Operator ID",
            "Permit/Monitoring Plan ID",
            "Account Holder Name",
            "Account Name",
            "Account Type",
            "Account Opening Date (UTC)",
            "Account Closure Date (UTC)",
            "Account Status",
            "Regulator",
            "First year of reporting emissions",
            "Last year of reporting emissions",
            "Cumulative Emissions",
            "Cumulative Surrendered Allowances",
            "Surrender Balance",
            "Current Surrender Status",
            "Validated ARs",
            "Enrolled ARs",
            "Suspended ARs",
            "Total ARs",
            "AR Changes (Nominations or Removals) in Progress",
            "AR Nomination Task IDs",
            "AR Nomination Task Claimants",
            "Most recent sign in (UTC)",
            "Total Available Quantity",
            "Total Reserved Quantity",
            "Surrender Transaction Pending Approval",
            "Receipt of Allowances Scheduled",
            "4 Eyes TAL Transactions",
            "Transactions Off The TAL Allowed",
            "4 Eyes Surrender/Return Of Excess Allocation",
            "Emissions 2021",
            "Emissions 2022",
            "Emissions 2023",
            "Emissions 2024",
            "Emissions 2025",
            "Emissions 2026",
            "Emissions 2027",
            "Emissions 2028",
            "Emissions 2029",
            "Emissions 2030",
            "Surrender Status 2021",
            "Surrender Status 2022",
            "Surrender Status 2023",
            "Surrender Status 2024",
            "Surrender Status 2025",
            "Surrender Status 2026",
            "Surrender Status 2027",
            "Surrender Status 2028",
            "Surrender Status 2029",
            "Surrender Status 2030"
        );
    }

    @Override
    public List<ComplianceManagementReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        List<ComplianceManagementReportData> results = mapper.mapData(reportQueryInfo);
        List<KeycloakUser> keycloakUsers = keycloakDbService.retrieveAllUsers();

        Map<String, LocalDateTime> userLastLoginMap = keycloakUsers.stream()
            .filter(user -> user.getLastLoginOn() != null)
            .collect(Collectors.toMap(
                KeycloakUser::getUrid,
                KeycloakUser::getLastLoginOn,
                (existing, replacement) -> existing.isAfter(replacement) ? existing : replacement // Keep the latest login
            ));

        results.forEach(result -> {
            String totalArsUris = result.getTotalArsUris();
            if (totalArsUris != null && !totalArsUris.isBlank()) {
                Set<String> arsSet = Arrays.stream(totalArsUris.split(","))
                    .map(String::trim)
                    .filter(t -> !t.isEmpty())
                    .collect(Collectors.toSet());

                LocalDateTime mostRecentLogin = arsSet.stream()
                    .map(userLastLoginMap::get)
                    .filter(Objects::nonNull)
                    .max(Comparator.naturalOrder())
                    .orElse(null);

                result.setMostRecentSignIn(mapper.formatLocalDateTime(mostRecentLogin));
            }
        });
        return results;
    }
}
