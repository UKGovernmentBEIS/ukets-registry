<#-- EXPIRED ACTIONS PAGE -->
<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>
    <#if section = "header">
        ${msg("errorTitle")}
    <#elseif section = "form">
        <div id="kc-error-message">
            <#--            <p class="instruction">${message.summary?no_esc}</p>-->
            <#if client?? && client.baseUrl?has_content>
                <p><a id="backToApplication" href="${client.baseUrl}">${kcSanitize(msg("backToApplication"))?no_esc}</a>
                </p>
            </#if>
            <p class="govuk-body"><a class="govuk-link" href="${properties.lostPasswordAnd2FADeviceUrl}">${msg("errorText")}</a></p>
        </div>
    </#if>
</@layout.registrationLayout>
