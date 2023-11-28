<#import "template.ftl" as layout>
    <@layout.registrationLayout; section>
        <#if section="header">

            <#elseif section="form">
                <form id="kc-otp-login-form" class="${properties.kcFormClass!}" action="${url.loginAction}"
                    method="post">
                    <#if otpLogin.userOtpCredentials?size gt 1>
                        <div class="${properties.kcFormGroupClass!}">
                            <div class="${properties.kcInputWrapperClass!}">
                                <#list otpLogin.userOtpCredentials as otpCredential>
                                    <div class="${properties.kcSelectOTPListClass!}">
                                    <input type="hidden" value="${otpCredential.id}">
                                        <div class="${properties.kcSelectOTPListItemClass!}">
                                            <span class="${properties.kcAuthenticatorOtpCircleClass!}"></span>
                                            <h2 class="${properties.kcSelectOTPItemHeadingClass!}">
                                                ${otpCredential.userLabel}
                                            </h2>
                                        </div>
                                    </div>
                                </#list>
                            </div>
                        </div>
                    </#if>

                    <#assign hasError = (message?has_content && message.summary?has_content && (message.type != 'warning' || !isAppInitiatedAction??))>
                    <div class="${properties.kcFormGroupClass!} ${hasError?then('govuk-form-group--error', '')}">
                    	<h1 class="${properties.kcLabelWrapperClass!}">
                    		<label class="${properties.kcLabelClass!} govuk-label--l" for="otp">
					      		${msg("loginOtpOneTimeTitle")}
					    	</label>
						</h1>         
					    <div id="otp-hint" class="govuk-hint">
					        ${msg("authenticatorCode")}
					    </div>
                        <#if hasError>
                            <span class="govuk-error-message" role="alert">
                                ${kcSanitize(message.summary)?no_esc}
                            </span>
                        </#if>
                        <div class="${properties.kcInputWrapperClass!}">
                            <input id="otpCode" name="otp" autocomplete="off" type="text" class="${properties.kcInputClass!} govuk-input--width-10"
                            autofocus/>
                        </div>
                    </div>

                     <details class="govuk-details" data-module="govuk-details">
                            <summary class="govuk-details__summary">
                                <span class="govuk-details__summary-text">
                                    ${msg("loginOtpDetailsTitle")}
                                </span>
                            </summary>
                            <div class="govuk-details__text"> 
                                <p class="govuk-!-margin-bottom-0">${msg("loginOtpDetailsSummary")}</p>
                                <p>${msg("loginOtpDetailsSummary2")}</p>
                            </div>
                        </details>

                    <div class="${properties.kcFormGroupClass!}">
                        <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                            <div class="${properties.kcFormOptionsWrapperClass!}">
                            </div>
                        </div>

                        <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                            <input
                                class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                                name="login" id="sign-in" type="submit" value="${msg("doLogIn")}" />
                        </div>
                    </div>
                </form>
            <script type="text/javascript" src="${url.resourcesCommonPath}/node_modules/jquery/dist/jquery.min.js"></script>
            <script type="text/javascript">
            $(document).ready(function() {
                // Card Single Select
                $('.card-pf-view-single-select').click(function() {
                  if ($(this).hasClass('active'))
                  { $(this).removeClass('active'); $(this).children().removeAttr('name'); }
                  else
                  { $('.card-pf-view-single-select').removeClass('active');
                  $('.card-pf-view-single-select').children().removeAttr('name');
                  $(this).addClass('active'); $(this).children().attr('name', 'selectedCredentialId'); }
                });

                var defaultCred = $('.card-pf-view-single-select')[0];
                if (defaultCred) {
                    defaultCred.click();
                }
              });
            </script>
        </#if>
        </@layout.registrationLayout>
