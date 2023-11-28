<html>
<head></head>
<body>
<#if completed>
<p>A request to transfer ${amount} units from account ${fullIdentifier} has been completed.</p>
<#else>
<p>A request to transfer ${amount} units from account ${fullIdentifier} has failed.</p>
</#if>
<#if requestId??>
<p>The request ID is ${requestId}.</p>
</#if>
<#if completed>
<p>The transaction ID is ${transactionId}.</p>
</#if>
<p>If you think this request should not have been made, you must <a href="mailto: ${etrAddress}">contact the UK Registry Helpdesk</a></p>
<p>This is an automatic email - please do not reply to this address.</p>
</body>
</html>