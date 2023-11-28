<#if requestType=="DOCUMENT_REQUEST">
A Registry Administrator has requested documents to be uploaded for <#if documentsRequestType=="ACCOUNT_HOLDER">${accountHolderName} of the ${accountName} Account.<#else>the user with ID ending in ${userId}.</#if>
The task ID is ${requestId}.
Please log in to the Registry to complete the task. Your document must be:
a PDF, PNG or JPG file
no more than 2MB
<#elseif requestType=="DOCUMENT_REQUEST_FINALISATION">
The request with ID ${requestId} to submit documentation for <#if documentsRequestType=="ACCOUNT_HOLDER">${accountHolderName} of the ${accountName} Account<#else>the user with ID ending in ${userId}</#if> has been completed.
Please login to the Registry and review the request.
</#if>
This is an automatic email - please do not reply to this address.