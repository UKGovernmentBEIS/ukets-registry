<html>
<head></head>
<body>
<#if requestType=="DOCUMENT_REQUEST">
<p>A Registry Administrator has requested documents to be uploaded for <#if documentsRequestType=="ACCOUNT_HOLDER">${accountHolderName}.<#else>the user with ID ending in ${userId}.</#if></p>
<p>The task ID is ${requestId}.</p>
<p>Please log in to the Registry to complete the task. Your document must be:</p>
<ul>
<li>a PDF, PNG or JPG file</li>
<li>no more than 2MB</li>
</ul>
<#elseif requestType=="DOCUMENT_REQUEST_FINALISATION">
<p>The request with ID ${requestId} to submit documentation for <#if documentsRequestType=="ACCOUNT_HOLDER">${accountHolderName}<#else>${userFullName}</#if> has been completed.</p>
<p>Please login to the Registry and review the request.</p>
</#if>
<p>This is an automatic email - please do not reply to this address.</p>
</body>
</html>