<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section="header">
            <#elseif section="form">
                <form id="kc-form-terms" class="${properties.kcFormClass!}" action="${url.loginAction}"
                method="post">
                    <div class="${properties.kcFormGroupClass!}">
                        <h1 class="${properties.kcLabelWrapperClass!}">
                            <label class="${properties.kcLabelClass!} govuk-label--l" for="acceptTermsConditions">
                                ${msg("termsTitle")}
                            </label>
                        </h1>
                        <div class="govuk-body">
                            ${msg("termsText")}
                            <a class="govuk-link" href="javascript:void(0);" > ${msg("termsText2")} </a>
                            ${msg("termsText3")}
                        </div>
                    </div>
                    <div class="${properties.kcFormGroupClass!}">
                        <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                            <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                                   id="acceptTermsConditions" name="acceptTermsConditions"
                                   type="submit" value="${msg("termsButton")}" />
                        </div>
                    </div>
                </form>
               </#if>
</@layout.registrationLayout>

