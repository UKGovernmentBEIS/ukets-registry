<html>
<head></head>
<body>
<#if taskOutcome=="APPROVED">
<#if isIssueAllowances>
<p>A request to issue ${units} UK allowances has been approved.</p>
<#else>
<p>The request to transfer ${units} units from account ${fullIdentifier} has been approved.</p>
<#if requestId??>
<p>The request ID is ${requestId}.</p>
</#if>
<p>The transaction ID is ${transactionId}.</p>
<p>The transaction will begin at ${date} ${time} (UTC).</p>
</#if>
<#elseif taskOutcome=="REJECTED">
<#if isIssueAllowances>
<p>A request to issue ${units} UK allowances has been rejected by ${rejectedBy}.</p>
<#else>
<p>The request to transfer ${units} units from account ${fullIdentifier} was rejected by ${rejectedBy}.</p>
<#if requestId??>
<p>The request ID is ${requestId}.</p>
</#if>
</#if>
</#if>
<p>If you think this request should not have been made, you must <a href="mailto: ${etrAddress}">contact the UK Registry Helpdesk</a></p>
<p>This is an automatic email - please do not reply to this address.</p>
</body>
</html>