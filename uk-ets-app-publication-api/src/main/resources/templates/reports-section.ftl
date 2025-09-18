<table class="govuk-table" aria-describedby="Domain events table">
    <thead class="govuk-table__head">
    <tr class="govuk-table__row">
        <#if isApplicableForYear == true || visibleReports?size gt 1>
            <th scope="col" class="govuk-table__header">Year</th>
        </#if>
        <#if yearToPeriod??>
            <th scope="col" class="govuk-table__header">Period (from — to)</th>
        </#if>
        <th scope="col" class="govuk-table__header">File</th>
        <th scope="col" class="govuk-table__header">Published on</th>
        <th scope="col" class="govuk-table__header">Size</th>
    </tr>
    </thead>
    <tbody class="govuk-table__body" id="visible-rows-${sectionType}">
        <#list visibleReports as reportFile>
            <tr class="govuk-table__row">
                <#if isApplicableForYear == true || visibleReports?size gt 1>
                    <td class="govuk-table__cell break-word">
                        ${(reportFile.applicableForYear?string.c)!}
                    </td>
                </#if>
                <#if yearToPeriod?? && reportFile.applicableForYear??>
                    <td class="govuk-table__cell">
                        ${yearToPeriod[reportFile.applicableForYear?c]!}
                    </td>
                </#if>
                <td class="govuk-table__cell break-word">
                    <img src="/assets/images/icon-file-download.png"
                         style="vertical-align: middle; margin-right: 10px"
                         alt="" />
                    <a class="govuk-link govuk-link--no-visited-state"
                       href="${(sectionPath)!}${(reportFile.fileName)!}"
                    >
                        <span class="govuk-visually-hidden">Download Excel file</span>
                        Download
                    </a>
                </td>
                <td class="govuk-table__cell date">
                    ${(reportFile.publishedOn?datetime("yyyy-MM-dd'T'hh:mm:ss")?string["d MMM yyyy"])!}
                </td>
                <td class="govuk-table__cell break-word">
                    ${(reportFile.fileSize)!}
                </td>
            </tr>
        </#list>
    </tbody>
    <tbody class="govuk-table__body" id="hidden-rows-${sectionType}" style="display: none;">
    <#list allReports as reportFile>
        <tr class="govuk-table__row">
            <#if isApplicableForYear == true || visibleReports?size gt 1>
                <td class="govuk-table__cell break-word">
                    ${(reportFile.applicableForYear?string.c)!}
                </td>
            </#if>
            <#if yearToPeriod?? && reportFile.applicableForYear??>
                <td class="govuk-table__cell">
                    ${yearToPeriod[reportFile.applicableForYear?c]!}
                </td>
            </#if>
            <td class="govuk-table__cell break-word">
                <img src="/assets/images/icon-file-download.png"
                     style="vertical-align: middle; margin-right: 10px"
                     alt="" />
                <a class="govuk-link govuk-link--no-visited-state"
                   href="${(sectionPath)!}${(reportFile.fileName)!}"
                >
                    <span class="govuk-visually-hidden">Download Excel file</span>
                    Download
                </a>
            </td>
            <td class="govuk-table__cell date">
                ${(reportFile.publishedOn?datetime("yyyy-MM-dd'T'hh:mm:ss")?string["d MMM yyyy"])!}
            </td>
            <td class="govuk-table__cell break-word">
                ${(reportFile.fileSize)!}
            </td>
        </tr>
    </#list>
    </tbody>
</table>
