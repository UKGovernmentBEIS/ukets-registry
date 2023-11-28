const PREFERENCES_SET_COOKIE = "uk_ets_cookies_preferences_set";
const COOKIES_POLICY = "uk_ets_cookies_policy";

/**
 * Display the pop up if the user has not accept the Cookies
 */
function checkIfCookiesNotAccepted() {
  if (notAccepted()) {
    // check if cookies are disabled by browser
    if (!navigator.cookieEnabled) {
      document.getElementById("acceptAllCookiesBtn").disabled = true;
      document.getElementById("setPreferencesCookiesBtn").disabled = true;
    }

    document.getElementById("global-cookie-message-to-accept").style.display =
      "block";
    disableSignInPageButtons(true);
  } else {
    document.getElementById("global-cookie-message-to-accept").style.display =
      "none";
    disableSignInPageButtons(false);
  }
}

function hideCookieMessage() {
  document.getElementById("global-cookie-message-approval").style.display =
    "none";
}

/**
 * Accept all cookies in the application.
 */
function acceptAllCookies() {
  if (!navigator.cookieEnabled) {
    return;
  }
  document.getElementById("global-cookie-message-approval").style.display =
    "block";
  document.getElementById("global-cookie-message-to-accept").style.display =
    "none";
  disableSignInPageButtons(false);
  const d = new Date();
  d.setTime(d.getTime() + 365 * 24 * 60 * 60 * 1000); // Valid for 1 year
  setCookie(PREFERENCES_SET_COOKIE, "true", {
    // secure: true,
    expires: d
  });
  setCookie(
    COOKIES_POLICY,
    JSON.stringify({
      essential: true,
      usage: true
    }),
    {
      // secure: true,
      expires: d
    }
  );
}

/**
 * Check if the Cookies have been accepted by the user
 */
function notAccepted() {
  return getCookie(PREFERENCES_SET_COOKIE) === null;
}

/**
 * Set a new Cookie
 * @param name The name of the Cookie
 * @param value The value of the Cookie
 * @param options Additional configuration of the Cookie
 */
function setCookie(name, value, options) {
  const cookieOptions = {
    path: "/",
    ...options
  };

  if (cookieOptions.expires instanceof Date) {
    cookieOptions.expires = cookieOptions.expires.toUTCString();
  }

  let updatedCookie = name + "=" + value;

  Object.keys(cookieOptions).forEach(option => {
    updatedCookie += "; " + option;
    let optionValue = cookieOptions[option];
    if (optionValue !== true) {
      updatedCookie += "=" + optionValue;
    }
  });

  document.cookie = updatedCookie;
}

/**
 * Get the value of a specific Cookie if exists
 * @param name The name of the Cookie
 */
function getCookie(name) {
  const cookieName = name + "=";
  const cookieValue = document.cookie.split(";");
  for (const cookie of cookieValue) {
    let c = cookie;
    while (c.charAt(0) === " ") {
      c = c.substring(1);
    }
    if (c.indexOf(cookieName) === 0) {
      return c.substring(cookieName.length, c.length);
    }
  }
  return null;
}

/**
 * Redirects to Set Preferences page
 */
function goToSetPreferences() {
  location.href = "/cookies";
}

/**
 * Disable/Enable buttons in sign in page depending on whether the user has accepted the Cookies.
 * @param disable true/false
 */
function disableSignInPageButtons(disable) {
  if (document.getElementById("sign-in")) {
    document.getElementById("sign-in").disabled = disable;
  }
  let forgotPasswordLink = "";
  if (document.getElementById("forgotPasswordPath")) {
    forgotPasswordLink = document.getElementById("forgotPasswordPath").value;
  }
  let lostAuthAppLink = "";
  if (document.getElementById("lostAuthAppLinkPath")) {
    lostAuthAppLink = document.getElementById("lostAuthAppLinkPath").value;
  }
  let lostAuthAndDeviceAppLink = "";
  if (document.getElementById("lostAuthAndDeviceAppLinkPath")) {
    lostAuthAndDeviceAppLink = document.getElementById("lostAuthAndDeviceAppLinkPath").value;
  }
  if (disable) {
    forgotPasswordLink = "javascript:void(0)";
    lostAuthAppLink = "javascript:void(0)";
    lostAuthAndDeviceAppLink = "javascript:void(0)";
  }
  if (document.getElementById("forgotPasswordLink")) {
    document.getElementById("forgotPasswordLink").href = forgotPasswordLink;
  }
  if (document.getElementById("lostAuthAppLink")) {
    document.getElementById("lostAuthAppLink").href = lostAuthAppLink;
  }
  if (document.getElementById("lostAuthAndDeviceAppLink")) {
    document.getElementById("lostAuthAndDeviceAppLink").href = lostAuthAndDeviceAppLink;
  }
}
