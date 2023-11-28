package gov.uk.ets.reports.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportType {

    R0001("Standard_Report_Accounts_No_ARs",ReportFormat.EXCEL),
    R0002("Standard_Report_Orphan_Users",ReportFormat.EXCEL),
    R0003("Standard_Report_Authorised_Representatives",ReportFormat.EXCEL),
    R0004("Standard_Report_All_Users",ReportFormat.EXCEL),
    R0005("Search_Tasks",ReportFormat.EXCEL),
    R0006("Search_Transactions",ReportFormat.EXCEL),
    R0007("Search_Accounts",ReportFormat.EXCEL),
    R0008("Standard_Report_Account_Details",ReportFormat.EXCEL),
    R0009("Standard_Report_Trusted_Accounts",ReportFormat.EXCEL),
    R0010("Standard_Report_KP_Paragraph_48",ReportFormat.EXCEL),
    R0011("Standard_Report_KP_Paragraph_45",ReportFormat.EXCEL),
    R0012("Standard_Report_Trading_Accounts",ReportFormat.EXCEL),
    R0013("Standard_Report_OHA_Participants_Allocations_",ReportFormat.EXCEL),
    R0014("Standard_Report_AOHA_Participants_Allocations_",ReportFormat.EXCEL),
    R0015("Standard_Report_Total_Volume_Allowances",ReportFormat.EXCEL),
    R0016("Standard_Report_Balance_Reports",ReportFormat.EXCEL),
    R0017("Standard_Report_Transaction_Volume",ReportFormat.EXCEL),
    R0018("Standard_Report_AR_Tasks",ReportFormat.EXCEL),
    R0019("Standard_Report_Verified_Emissions_Surrendered_Allowances",ReportFormat.EXCEL),
    R0020("Kyoto_Protocol_SEF_Report",ReportFormat.EXCEL),
    R0021("Kyoto_Protocol_R2_Discrepant_Transaction_Report",ReportFormat.EXCEL),
    R0022("Kyoto_Protocol_R3_Notification_List_Report",ReportFormat.EXCEL),
    R0023("Kyoto_Protocol_R4_Reconciliation_List_Report",ReportFormat.EXCEL),
    R0024("Kyoto_Protocol_R5_Unit_Conversion_Report",ReportFormat.EXCEL),
    R0025("Standard_Report_Compliance_10_years_report",ReportFormat.EXCEL),
    R0026("Search_Tasks",ReportFormat.EXCEL),
    R0027("Standard_Report_Allocations",ReportFormat.EXCEL),
    R0028("Standard_Report_Compliance_Surrenders",ReportFormat.EXCEL),
    R0029("Standard_Report_Unit_Block",ReportFormat.EXCEL),
    R0030("Standard_Report_Allocation_Preparation",ReportFormat.EXCEL),
    R0031("Standard_Report_Compliance_Emissions",ReportFormat.EXCEL),
    R0032("Admin_Standard_Report_Compliance_Surrender",ReportFormat.EXCEL),
    R0033("Admin_Standard_Report_Compliance_Emissions",ReportFormat.EXCEL),
    R0034("Transaction_Details",ReportFormat.PDF),
    R0035("Account_Statement",ReportFormat.PDF),
    R0036("Standard_Report_Users_Submitted_Documents",ReportFormat.EXCEL),
    R0037("Standard_Report_Account_Holders_Submitted_Documents",ReportFormat.EXCEL),
    R0038("Standard_Report_Allocation_Transactions_AU",ReportFormat.EXCEL),
    R0039("Standard_Report_Task_List_Current_Report",ReportFormat.EXCEL);

    private final String name;
    private final ReportFormat format;

    public enum ReportFormat {
        EXCEL {
            public String getFileExtension() {
                return "xlsx";
            }
        },
        PDF {
            public String getFileExtension() {
                return "pdf";
            }
        };

        public abstract String getFileExtension();

    };
}

