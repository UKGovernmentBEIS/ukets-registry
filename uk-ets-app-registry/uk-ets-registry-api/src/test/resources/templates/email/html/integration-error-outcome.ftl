<html>
<head><style>
.tab {
    margin-left: 30px;
}
</style></head>
<body>
<p>An error has been identified: </p>
<p>Integration Point Name: <b>${integrationPoint}</b></p>
<p>Message Payload
<ul>
<#list fields?keys as key>
    <li>${key}: <b>${fields[key]}</b></li>
</#list>
</ul>
</p>
<p>Error Code:
    <div class="tab">For action:
        <ul>
        <#list actionErrors as error>
            <li><b>${error.getError().name()}, ${error.getError().getMessage()}<#if error.getErrorMessage()??>: ${error.getErrorMessage()}</#if></b></li>
        </#list>
        </ul>
    </div>
    <div class="tab">For information:
        <ul>
        <#list infoErrors as error>
            <li><b>${error.getError().name()}, ${error.getError().getMessage()}<#if error.getErrorMessage()??>: ${error.getErrorMessage()}</#if></b></li>
        </#list>
        </ul>
    </div>
</p>
<p>Date: <b>${date}</b><br>Time: <b>${time}</b></p>
<p>MessageID (correlation ID): <b>${correlationId}</b>
<br>Source System Identifier: <b>${sourceSystem}</b>
<br>Message Processing Status: <b>ERROR</b></p>
<p>Intended recipients:
<ul>
    <li>For action: ${actionRecipients}</li>
    <li>For information: ${infoRecipients}</li>
</ul>
</p>
<#if (hasRegistryActionRecipient || hasServiceDeskOperator || hasTuSupport)>
<p>Actions:
<ul>
    <#if (hasRegistryActionRecipient)><li>For Registry Administrators: proceed with any manual action needed</li></#if>
    <#if (hasServiceDeskOperator)><li>For service desk operator (Fordway): contact the development team for root cause investigation</li></#if>
    <#if (hasTuSupport)><li>For TU SD/Support: investigate the root cause of the error</li></#if>
</ul>
</p>
</#if>
</body>
</html>