<#if approved>
Your request to open a new UK Registry account has been approved.
The new account ID is ${accountId}.
If you did not make this request, or if you are not the authorised representative for this account, you must contact the UK Registry Helpdesk at ${etrAddress}
<#else>
Your request to open a new UK Registry account has been rejected for the following reason:
${reason}
If you did not make this request you must contact the UK Registry Helpdesk at ${etrAddress}
</#if>
This is an automatic email - please do not reply to this address.