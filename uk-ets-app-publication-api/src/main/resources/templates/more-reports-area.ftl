<details class="govuk-details" data-module="govuk-details">
    <summary class="govuk-details__summary public-reports-summary">
        <span class="govuk-details__summary-text"> See all files </span>
    </summary>
    <div class="govuk-details__text public-reports-details">
        <#list moreReportFiles as reportFile>
            <div class="govuk-!-padding-bottom-3">
                <img src="/assets/images/icon-file-download.png"
                     style="vertical-align: middle; margin-right: 10px"
                     alt=""
                />
                <a class="govuk-link govuk-link--no-visited-state"
                   href="${(sectionPath)!}${(reportFile.fileName)!}">
                    <span class="govuk-visually-hidden">Download Excel file</span>
                    ${(reportFile.fileName)!}, ${(reportFile.fileSize)!}
                </a>
            </div>
        </#list>
    </div>
</details>
