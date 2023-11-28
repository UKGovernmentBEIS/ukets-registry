const GTAG_URL = 'https://www.googletagmanager.com/gtag/js?id=';
var TRACKING_ID = '';
const SIGN_IN = '/sign-in';
const SIGNED_IN = '/signed-in';
const GA_DURATION_COOKIE = 'sign_in_timings';
const GA_LOGIN_COOKIE = 'already_logged';
const EVENT_CATEGORY = 'first time sign in';

if(document.getElementById('kc-form-login')) {
    TRACKING_ID = document.getElementById('kc-form-login').getAttribute('trackid');
}

if(document.getElementById('kc-totp-settings-form')) {
    TRACKING_ID = document.getElementById('kc-totp-settings-form').getAttribute('trackid');
}

window.dataLayer = window.dataLayer || [];
function gtag(){dataLayer.push(arguments);}
gtag('js', new Date());
gtag('config', TRACKING_ID, {send_page_view: false});

(function(){
    loadGA();
})();

// Install Google Analytics gtag.js
function loadGA() {
    if (!navigator.cookieEnabled) {
        return;
    }

    if(typeof notAccepted !== undefined && !notAccepted()) {
        var ga=document.createElement('script');
        ga.type='text/javascript';
        ga.async=true;
        ga.src= GTAG_URL + TRACKING_ID;
        var s=document.getElementsByTagName('script')[0];s.parentNode.insertBefore(ga,s);
    }
}

if(document.getElementById("acceptAllCookiesBtn")) {
    document.getElementById("acceptAllCookiesBtn").addEventListener("click", () => loadGA());
}

if(document.getElementById("sign-in")) {
    document.getElementById("sign-in").addEventListener("click", () => {
        initializeCookie();

        gtag('event', 'page_view', { page_path: SIGN_IN });
        
        if(!getCookie(GA_LOGIN_COOKIE)) {
            gtag('event', EVENT_CATEGORY + ' started', { event_category: EVENT_CATEGORY });
        }
    });
}

if( document.getElementById("saveTOTPBtn")) {
    document.getElementById("saveTOTPBtn").addEventListener("click", () => {        
        sendDuration();
        gtag('event', 'page_view', { page_path: SIGNED_IN });
    });
}

function initializeCookie() {
    if(typeof setCookie == undefined) {
        return;
    };
    var date = new Date();
    var now = Date.now();
    date.setMinutes(date.getMinutes()+15);

    // set a timestamp cookie    
    setCookie(GA_DURATION_COOKIE, now,{expires: date.toUTCString()});   
}

function sendDuration() {
    if(typeof getCookie == undefined) {
        return;
    };
    var now = Date.now();

    var date = new Date();
    date.setTime(date.getTime() + 365 * 24 * 60 * 60 * 1000);

    // get a timestamp cookie 
    if(getCookie(GA_DURATION_COOKIE) && !getCookie(GA_LOGIN_COOKIE)) { 
        var actionStarted = Number(getCookie(GA_DURATION_COOKIE));
        var duration = now - actionStarted;
        gtag('event', 'timing_complete', { name: 'completed', event_category: EVENT_CATEGORY, value: duration });
        
        // send event for firts sign in completion
        gtag('event', EVENT_CATEGORY + ' ended', { event_category: EVENT_CATEGORY, event_label: 'goal completed' });

         // set a timestamp cookie for first time logged in 
        setCookie(GA_LOGIN_COOKIE, {expires: date});
    }
}