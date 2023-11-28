A request has been made to transfer ${units} units from account ${fullIdentifier}.
<#if requestId??>
The request ID is ${requestId}.
</#if>
The transaction ID is ${transactionId}.
<#if approvalRequired>
The transaction must now be approved by another user before it can be executed.
<#else>
The transaction will begin at ${date} GMT on ${time}.
</#if>
If you think this request should not have been made, you must contact the UK Registry Helpdesk at ${etrAddress}
This is an automatic email - please do not reply to this address.
