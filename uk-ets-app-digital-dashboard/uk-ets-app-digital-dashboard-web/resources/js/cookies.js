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
  } else {
    document.getElementById("global-cookie-message-to-accept").style.display =
      "none";
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
  location.href = '/cookies';
}
