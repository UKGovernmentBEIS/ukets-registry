<#if taskOutcome=="APPROVED">
<#if isIssueAllowances>
A request to issue ${units} UK allowances has been approved.
<#else>
The request to transfer ${units} units from account ${fullIdentifier} has been approved.
<#if requestId??>
The request ID is ${requestId}.
</#if>
The transaction ID is ${transactionId}.
The transaction will begin at ${date} GMT on ${time}.
</#if>
<#elseif taskOutcome=="REJECTED">
<#if isIssueAllowances>
A request to issue ${units} UK allowances has been rejected by ${rejectedBy}.
<#else>
The request to transfer ${units} units from account ${fullIdentifier} was rejected by ${rejectedBy}.
<#if requestId??>
The request ID is ${requestId}.
</#if>
</#if>
</#if>
If you think this request should not have been made, you must contact the UK Registry Helpdesk at ${etrAddress}
This is an automatic email - please do not reply to this address.