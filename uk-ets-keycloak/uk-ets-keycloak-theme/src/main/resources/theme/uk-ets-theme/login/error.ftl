<#-- EXPIRED ACTIONS PAGE -->
<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>
    <#if section = "header">
        ${msg("errorTitle")}
    <#elseif section = "form">
        <div id="kc-error-message">
            <#if client?? && client.baseUrl?has_content>
                <p>
                	<a id="backToApplication" href="${client.baseUrl}">${kcSanitize(msg("backToApplication"))?no_esc}</a>
                </p>
                <p class="govuk-body">${kcSanitize(message.summary)?no_esc}</p>
            </#if>
            <#if client?? && client.clientId?has_content>
            	<p class="govuk-body"><a class="govuk-link" href="${properties.lostPasswordAnd2FADeviceUrl}${client.clientId}">${msg("errorText")}</a></p>
            <#else>
            	<p class="govuk-body">${kcSanitize(message.summary)?no_esc}</p>
            </#if>
        </div>
    </#if>
</@layout.registrationLayout>
