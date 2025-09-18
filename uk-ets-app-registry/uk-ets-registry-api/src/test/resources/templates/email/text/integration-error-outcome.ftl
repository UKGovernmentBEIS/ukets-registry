An error has been identified:

Integration Point Name: ${integrationPoint}

Message Payload
<#list fields?keys as key>
  - ${key}: ${fields[key]}
</#list>

Error Code:
For action:
<#list actionErrors as error>
  - ${error.getError().name()}, ${error.getError().getMessage()}<#if error.getErrorMessage()??>: ${error.getErrorMessage()}</#if>
</#list>

For information:
<#list infoErrors as error>
  - ${error.getError().name()}, ${error.getError().getMessage()}<#if error.getErrorMessage()??>: ${error.getErrorMessage()}</#if>
</#list>

Date: ${date}
Time: ${time}

MessageID (correlation ID): ${correlationId}
Source System Identifier: ${sourceSystem}

Message Processing Status: ERROR

Intended recipients:
  - For action: ${actionRecipients}
  - For information: ${infoRecipients}

<#if (hasRegistryActionRecipient || hasServiceDeskOperator || hasTuSupport)>
Actions:
<#if (hasRegistryActionRecipient)> - For Registry Administrators: proceed with any manual action needed</#if>
<#if (hasServiceDeskOperator)> - For service desk operator (Fordway): contact the development team for root cause investigation</#if>
<#if (hasTuSupport)> - For TU SD/Support: investigate the root cause of the error</#if>
</#if>