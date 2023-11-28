<html>
<head></head>
<body>
<#if approved>
<p>Your request to open a new UK Registry account has been approved.</p>
<p>The new account ID is ${accountId}.</p>
<p>If you did not make this request, or if you are not the authorised representative for this account, you must <a href="mailto: ${etrAddress}">contact the UK Registry Helpdesk</a></p>
<#else>
<p>Your request to open a new UK Registry account has been rejected for the following reason:</p>
<p>${reason}</p>
<p>If you did not make this request you must <a href="mailto: ${etrAddress}">contact the UK Registry Helpdesk</a></p>
</#if>
<p>This is an automatic email - please do not reply to this address.</p>
</body>
</html>