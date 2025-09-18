<#macro registrationLayout bodyClass="" displayInfo=false displayMessage=true displayRequiredFields=false displayWide=false showAnotherWayIfPresent=true>
    <!DOCTYPE html>
    <html lang="en" class="govuk-template ">

    <head>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
        <meta name="theme-color" content="#0b0c0c"/>
        <meta http-equiv="X-UA-Compatible" content="IE=edge"/>

        <link rel="shortcut icon" sizes="16x16 32x32 48x48" href="${url.resourcesPath}/img/favicon.ico"
              type="image/x-icon"/>
        <link rel="mask-icon" href="${url.resourcesPath}/img/govuk-mask-icon.svg" color="#0b0c0c">
        <link rel="apple-touch-icon" sizes="180x180" href="${url.resourcesPath}/img/govuk-apple-touch-icon-180x180.png">
        <link rel="apple-touch-icon" sizes="167x167" href="${url.resourcesPath}/img/govuk-apple-touch-icon-167x167.png">
        <link rel="apple-touch-icon" sizes="152x152" href="${url.resourcesPath}/img/govuk-apple-touch-icon-152x152.png">
        <link rel="apple-touch-icon" href="${url.resourcesPath}/img/govuk-apple-touch-icon.png">

        <meta property="og:image" content="${url.resourcesPath}/img/govuk-opengraph-image.png">

        <#if properties.meta?has_content>
            <#list properties.meta?split(' ') as meta>
                <meta name="${meta?split('==')[0]}" content="${meta?split('==')[1]}"/>
            </#list>
        </#if>
        <title>${msg("loginTitle")}</title>
        <link rel="icon" href="${url.resourcesPath}/img/favicon.ico"/>

        <#if properties.styles?has_content>
            <#list properties.styles?split(' ') as style>
                <link href="${url.resourcesPath}/${style}" rel="stylesheet"/>
            </#list>
        </#if>

        <#if properties.scripts?has_content>
            <#list properties.scripts?split(' ') as script>
                <script src="${url.resourcesPath}/${script}" type="text/javascript"></script>
            </#list>
        </#if>

        <#if scripts??>
            <#list scripts as script>
                <script src="${script}" type="text/javascript"></script>
            </#list>
        </#if>

        <script type="module">
            import { initAll } from '${url.resourcesPath}/${properties.govukScript}';
            initAll();
        </script>

    </head>

    <body class="govuk-template__body govuk-frontend-supported" onload="checkIfCookiesNotAccepted()">

    <script>
      document.body.className = ((document.body.className) ? document.body.className + ' js-enabled' : 'js-enabled');
    </script>

    <div id="global-cookie-message-to-accept" class="gem-c-cookie-banner" role="region" aria-label="cookie banner"
         style="display: none;">
        <div class="govuk-width-container">
            <div class="govuk-grid-row">
                <div class="govuk-grid-column-two-thirds govuk-grid-column-two-thirds-custom">
                    <div>
                        <h2 class="govuk-heading-m">${msg("cookieMessage4")}</h2>
                        <p class="govuk-body">${msg("weUse")} ${msg("cookieMessage1")} ${msg("cookiesInformation")}
                        </p>
                    </div>
                    <div>
                        <div class="govuk-grid-column-one-half-from-desktop">
                            <button class="govuk-button govuk-cookie-button" id="acceptAllCookiesBtn"
                                    onclick="acceptAllCookies()">
                                ${msg("acceptCookies")}
                            </button>
                        </div>
                        <div class="govuk-grid-column-one-half-from-desktop">
                            <button class="govuk-button govuk-cookie-button" id="setPreferencesCookiesBtn"
                                    onclick="goToSetPreferences()">
                                ${msg("setCookies")}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div id="global-cookie-message-approval" class="gem-c-cookie-banner govuk-clearfix" style="display: none;">
        <div class="govuk-width-container">
            <div class="govuk-grid-row">
                <div class="govuk-grid-column-two-thirds govuk-grid-column-two-thirds-custom">
                    <p>${msg("acceptCookieInformation")}
                        <a class="govuk-link" href="javascript:void(0);"
                           onclick="goToSetPreferences()">${msg("cookieMessage2")}</a>
                        ${msg("cookieMessage3")}
                    </p>
                </div>
                <div class="govuk-grid-column-one-third">
                    <p>
                        <button class="gem-c-cookie-banner__hide-button" onclick="hideCookieMessage()">${msg("hide")}
                        </button>
                    </p>
                </div>
            </div>
        </div>
    </div>
    <header id="header" class="govuk-header " role="banner" data-module="govuk-header">
        <div class="govuk-header__container govuk-width-container">
            <div class="govuk-header__logo">
                <a href="/" class="govuk-header__link govuk-header__link--homepage">
	          <span class="govuk-header__logotype">
	            <svg role="presentation" focusable="false" class="govuk-header__logotype-crown"
                     xmlns="http://www.w3.org/2000/svg" viewBox="0 0 32 30" height="30" width="32">
                    <path fill="currentColor" fill-rule="evenodd"
                        d="M22.6 10.4c-1 .4-2-.1-2.4-1-.4-.9.1-2 1-2.4.9-.4 2 .1 2.4 1s-.1 2-1 2.4m-5.9 6.7c-.9.4-2-.1-2.4-1-.4-.9.1-2 1-2.4.9-.4 2 .1 2.4 1s-.1 2-1 2.4m10.8-3.7c-1 .4-2-.1-2.4-1-.4-.9.1-2 1-2.4.9-.4 2 .1 2.4 1s0 2-1 2.4m3.3 4.8c-1 .4-2-.1-2.4-1-.4-.9.1-2 1-2.4.9-.4 2 .1 2.4 1s-.1 2-1 2.4M17 4.7l2.3 1.2V2.5l-2.3.7-.2-.2.9-3h-3.4l.9 3-.2.2c-.1.1-2.3-.7-2.3-.7v3.4L15 4.7c.1.1.1.2.2.2l-1.3 4c-.1.2-.1.4-.1.6 0 1.1.8 2 1.9 2.2h.7c1-.2 1.9-1.1 1.9-2.1 0-.2 0-.4-.1-.6l-1.3-4c-.1-.2 0-.2.1-.3m-7.6 5.7c.9.4 2-.1 2.4-1 .4-.9-.1-2-1-2.4-.9-.4-2 .1-2.4 1s0 2 1 2.4m-5 3c.9.4 2-.1 2.4-1 .4-.9-.1-2-1-2.4-.9-.4-2 .1-2.4 1s.1 2 1 2.4m-3.2 4.8c.9.4 2-.1 2.4-1 .4-.9-.1-2-1-2.4-.9-.4-2 .1-2.4 1s0 2 1 2.4m14.8 11c4.4 0 8.6.3 12.3.8 1.1-4.5 2.4-7 3.7-8.8l-2.5-.9c.2 1.3.3 1.9 0 2.7-.4-.4-.8-1.1-1.1-2.3l-1.2 4c.7-.5 1.3-.8 2-.9-1.1 2.5-2.6 3.1-3.5 3-1.1-.2-1.7-1.2-1.5-2.1.3-1.2 1.5-1.5 2.1-.1 1.1-2.3-.8-3-2-2.3 1.9-1.9 2.1-3.5.6-5.6-2.1 1.6-2.1 3.2-1.2 5.5-1.2-1.4-3.2-.6-2.5 1.6.9-1.4 2.1-.5 1.9.8-.2 1.1-1.7 2.1-3.5 1.9-2.7-.2-2.9-2.1-2.9-3.6.7-.1 1.9.5 2.9 1.9l.4-4.3c-1.1 1.1-2.1 1.4-3.2 1.4.4-1.2 2.1-3 2.1-3h-5.4s1.7 1.9 2.1 3c-1.1 0-2.1-.2-3.2-1.4l.4 4.3c1-1.4 2.2-2 2.9-1.9-.1 1.5-.2 3.4-2.9 3.6-1.9.2-3.4-.8-3.5-1.9-.2-1.3 1-2.2 1.9-.8.7-2.3-1.2-3-2.5-1.6.9-2.2.9-3.9-1.2-5.5-1.5 2-1.3 3.7.6 5.6-1.2-.7-3.1 0-2 2.3.6-1.4 1.8-1.1 2.1.1.2.9-.3 1.9-1.5 2.1-.9.2-2.4-.5-3.5-3 .6 0 1.2.3 2 .9l-1.2-4c-.3 1.1-.7 1.9-1.1 2.3-.3-.8-.2-1.4 0-2.7l-2.9.9C1.3 23 2.6 25.5 3.7 30c3.7-.5 7.9-.8 12.3-.8z" />
                    <image src="${url.resourcesPath}/img/govuk-logotype-crown.png" xlink:href=""
                            class="govuk-header__logotype-crown-fallback-image" width="32" height="30"></image>
	            </svg>
	            <span class="govuk-header__logotype-text">
	              GOV.UK
	            </span>
	          </span>
                </a>
            </div>
        </div>
    </header>

    <div class="govuk-width-container">
        <main class="govuk-main-wrapper " id="main-content" role="main">
            <div class="govuk-grid-row">
                <div class="govuk-grid-column-two-thirds govuk-grid-column-two-thirds-custom">
                    <#assign isNotWarningOrAppInitiated = (displayMessage && message?has_content && message.summary?has_content && (message.type != 'warning' || !isAppInitiatedAction??))>
                    <#-- TODO Could not find another way to check that this is the update password action. -->
                    <#assign isPasswordUpateAction = message?has_content && message.summary?has_content && message.summary == 'You need to change your password to activate your account.'>

                    <#if isNotWarningOrAppInitiated && isPasswordUpateAction>
                        <span class="govuk-caption-xl">${msg("updatePasswordCaption")}</span>
                    </#if>
                    <h1 id="kc-page-title" class="govuk-heading-l"><#nested "header"></h1>
                    <#if isNotWarningOrAppInitiated && isPasswordUpateAction>
                        <p class="govuk-body govuk-!-margin-bottom-7">${msg("updatePasswordNotePart1")} ${properties.passwordMinChars!} ${msg("updatePasswordNotePart2")}</p>
                    </#if>
                    <#-- App-initiated actions should not see warning messages about the need to complete the action -->
                    <#-- during login.  The update password action  is apparently NOT an app initiated action, thus the extra check -->
                    <#if isNotWarningOrAppInitiated && !isPasswordUpateAction>
                        <div class="govuk-error-summary" aria-labelledby="error-summary-title" role="alert" tabindex="-1"
                             data-module="govuk-error-summary">
                            <h2 class="govuk-error-summary__title" id="error-summary-title">
                                There is a problem
                            </h2>
                            <div class="govuk-error-summary__body">
                                <ul class="govuk-list govuk-error-summary__list">
                                    <li>
                                        <a href="javascript:void(0)" id="errorLink" onclick="navigateToInputField('${kcSanitize(message.summary)?js_string}')">
                                            ${kcSanitize(message.summary)?no_esc}
                                        </a>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </#if>

                    <#if displayInfo>
                        <div id="kc-info" class="${properties.kcSignUpClass!}">
                            <div id="kc-info-wrapper" class="${properties.kcInfoAreaWrapperClass!}">
                                <#nested "info">
                            </div>
                        </div>
                    </#if>

                    <#nested "form">

                    <#if displayInfo>
                        <div id="kc-problems-sign-in" class="${properties.kcSignUpClass!}">
                            <div id="kc-problems-sign-in-wrapper" class="${properties.kcInfoAreaWrapperClass!}">
                                <div id="kc-solutions" class="govuk-body">
                                    <h4 class="govuk-heading-s">${msg("problemsSignIn")}</h4>
                                    <ul class="govuk-list">
                                        <li>
                                            <a id="forgotPasswordLink" class="govuk-link"
                                               href="${properties.forgotPasswordUrl}">${msg("passwordForgot")}</a>
                                            <input type="hidden" id="forgotPasswordPath"
                                                   value="${properties.forgotPasswordUrl}"/>
                                        </li>
                                        <li>
                                            <a id="lostAuthAppLink" class="govuk-link"
                                               href="${properties.lost2FADeviceUrl}">${msg("2FADeviceLost")}</a>
                                            <input type="hidden" id="lostAuthAppLinkPath"
                                                   value="${properties.lost2FADeviceUrl}"/>
                                        </li>
                                        <li>
                                            <a id="lostAuthAndDeviceAppLink" class="govuk-link"
                                               href="${properties.lostPasswordAnd2FADeviceUrl}${client.clientId}">
                                                ${msg("PasswordAnd2FADeviceLost")}</a>
                                            <input type="hidden" id="lostAuthAndDeviceAppLinkPath"
                                                   value="${properties.lostPasswordAnd2FADeviceUrl}${client.clientId}"/>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </#if>

                    <a
                            class="govuk-link app-c-back-to-top dont-print govuk-link--no-visited-state"
                            href="#"
                            onkeypress="backToTop();"
                            onclick="backToTop();"
                    >
                        <svg
                                class="app-c-back-to-top__icon"
                                xmlns="http://www.w3.org/2000/svg"
                                width="13"
                                height="17"
                                viewBox="0 0 13 17"
                        >
                            <path
                                    fill="currentColor"
                                    d="M6.5 0L0 6.5 1.4 8l4-4v12.7h2V4l4.3 4L13 6.4z"
                            ></path>
                        </svg>
                        Back to top
                    </a>
                </div>
            </div>
        </main>
    </div>
    </div>

    <footer class="govuk-footer " role="contentinfo">
        <div class="govuk-width-container ">
            <div class="govuk-footer__meta">
                <div class="govuk-footer__meta-item govuk-footer__meta-item--grow">
                    <h2 class="govuk-visually-hidden">Support links</h2>
                    <ul class="govuk-footer__inline-list">
                        <li class="govuk-footer__inline-list-item">
                            <a class="govuk-footer__link" href="/privacy">
                                Privacy Notice
                            </a>
                        </li>
                        <li class="govuk-footer__inline-list-item">
                            <a class="govuk-footer__link" href="/contact-us">
                                Contact us
                            </a>
                        </li>
                        <li class="govuk-footer__inline-list-item">
                            <a class="govuk-footer__link" href="/accessibility">
                                Accessibility Statement
                            </a>
                        </li>
                    </ul>

                    <svg role="presentation" focusable="false" class="govuk-footer__licence-logo"
                         xmlns="http://www.w3.org/2000/svg" viewbox="0 0 483.2 195.7" height="17" width="41">
                        <path fill="currentColor"
                              d="M421.5 142.8V.1l-50.7 32.3v161.1h112.4v-50.7zm-122.3-9.6A47.12 47.12 0 0 1 221 97.8c0-26 21.1-47.1 47.1-47.1 16.7 0 31.4 8.7 39.7 21.8l42.7-27.2A97.63 97.63 0 0 0 268.1 0c-36.5 0-68.3 20.1-85.1 49.7A98 98 0 0 0 97.8 0C43.9 0 0 43.9 0 97.8s43.9 97.8 97.8 97.8c36.5 0 68.3-20.1 85.1-49.7a97.76 97.76 0 0 0 149.6 25.4l19.4 22.2h3v-87.8h-80l24.3 27.5zM97.8 145c-26 0-47.1-21.1-47.1-47.1s21.1-47.1 47.1-47.1 47.2 21 47.2 47S123.8 145 97.8 145"/>
                    </svg>
                    <span class="govuk-footer__licence-description">
            All content is available under the
            <a class="govuk-footer__link"
               href="https://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/" rel="license">Open Government Licence v3.0</a>, except where otherwise stated
          </span>
                </div>
                <div class="govuk-footer__meta-item">
                    <a class="govuk-footer__link govuk-footer__copyright-logo"
                       href="https://www.nationalarchives.gov.uk/information-management/re-using-public-sector-information/uk-government-licensing-framework/crown-copyright/">©
                        Crown copyright</a>
                </div>
            </div>
        </div>
    </footer>

    <script>
      function navigateToInputField(message) {
          let field = '';
          switch (message){
              case '${msg("invalidUserMessage")}':
              case '${msg("accountDisabledMessage")}':
              case '${msg("passwdBlacklistedMessage")}':
              case '${msg("loginTimeout")}':
                  field = '#username';
                  break;
              case '${msg("missingTotpMessage")}':
              case '${msg("invalidTotpMessage")}':
                  if (document.getElementById("totp")) {
                      field = '#totp';
                  } else if (document.getElementById("otp")) {
                      field = '#otp';
                  }
                  break;
              case '${msg("missingPasswordMessage")}':
              case '${msg("notMatchPasswordMessage")}':
              case '${msg("resetPasswordMessage")}':
              case 'Enter a strong password':
                  field = '#password-new';
                  break;
              case '${msg("passwdNotComplyingPolicyMessage")}':
                  field = '#forgotPasswordLink';
                  break;
              default:
                  field = 'javascript:void(0)';
          }
          if (document.getElementById("errorLink")) {
              document.getElementById('errorLink').href = field;
          }
      }
    </script>
    <#if properties.bodyScripts?has_content>
        <#list properties.bodyScripts?split(' ') as bodyScript>
            <script src="${url.resourcesPath}/${bodyScript}" type="text/javascript"></script>
        </#list>
    </#if>
    </body>
    </html>
</#macro>
