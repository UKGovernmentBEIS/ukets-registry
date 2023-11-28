<html>
<head></head>
<body>
<p>A request <#if taskOutcome=="">has been made </#if>to update the ${requestType} for account ${fullIdentifier}<#if taskOutcome!=""> has been ${taskOutcome}</#if>.</p>
<p>The request ID is ${requestId}.</p>
<p>If you think this request should not have been made, you must <a href="mailto: ${etrAddress}">contact the UK Registry Helpdesk</a></p>
<p>This is an automatic email - please do not reply to this address.</p>
</body>
</html>