<html>
<body>
${kcSanitize(msg("eventLoginErrorBodyText", event.ipAddress, event.date))?no_esc} <a href="mailto: ${msg("etrAddress")}">${msg("eventLoginErrorLinkText")} </a>
</body>
</html>
