<table class="govuk-table" aria-describedby="Domain events table">
    <thead class="govuk-table__head">
    <tr class="govuk-table__row">
        <#if reportFiles?size gt 1>
            <th scope="col" class="govuk-table__header">Year</th>
        </#if>
        <th scope="col" class="govuk-table__header">File</th>
        <th scope="col" class="govuk-table__header">Published on</th>
        <th scope="col" class="govuk-table__header">Size</th>
    </tr>
    </thead>
    <tbody class="govuk-table__body">
        <#list reportFiles as reportFile>
            <tr class="govuk-table__row">
                <#if reportFiles?size gt 1>
                    <td class="govuk-table__cell break-word">
                        ${(reportFile.applicableForYear?string.c)!}
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