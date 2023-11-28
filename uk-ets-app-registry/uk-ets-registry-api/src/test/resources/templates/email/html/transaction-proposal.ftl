<html>
<head></head>
<body>
<p>A request has been made to transfer ${units} units from account ${fullIdentifier}.</p>
<#if requestId??>
<p>The request ID is ${requestId}.</p>
</#if>
<p>The transaction ID is ${transactionId}.</p>
<#if approvalRequired>
<p>The transaction must now be approved by another user before it can be executed.</p>
<#else>
<p>The transaction will begin at ${date} GMT on ${time}.</p>
</#if>
<p>If you think this request should not have been made, you must <a href="mailto: ${etrAddress}">contact the UK Registry Helpdesk</a></p>
<p>This is an automatic email - please do not reply to this address.</p>
</body>
</html>