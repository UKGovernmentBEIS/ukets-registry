<#if completed>
A request to transfer ${amount} units from account ${fullIdentifier} has been completed.
<#else>
A request to transfer ${amount} units from account ${fullIdentifier} has failed.
</#if>
<#if requestId??>
The request ID is ${requestId}.
</#if>
<#if completed>
The transaction ID is ${transactionId}.
</#if>
If you think this request should not have been made, you must contact the UK Registry Helpdesk at ${etrAddress}
This is an automatic email - please do not reply to this address.